from typing import Optional

from sqlalchemy.orm import Session, selectinload
from sqlalchemy import select

from app.models.order_model import OrderModel
from app.models.cart_item_model import CartItemModel
from app.repositories import order_item_repository
from app.schemas.order_schema import OrderCreate, OrderCreateNoCart, OrderResponse
from app.schemas.gained_reward_schema import GainedRewardCreate
from app.repositories.gained_reward_repository import create_gained_reward
from app.schemas.cart_item_schema import CartItemCreateForPromotion

def get_order_by_id(db: Session, order_id: int, user_id: int, status: Optional[str] = None) -> Optional[OrderModel]:
    stmt = (
        select(OrderModel)
        .where(OrderModel.id == order_id, OrderModel.user_id == user_id)
        .options(selectinload(OrderModel.order_items))
    )
    if status is not None:
        stmt = stmt.where(OrderModel.status == status)
    return db.scalars(stmt).first()


def get_all_orders(db: Session, user_id: int, status: Optional[str] = None) -> list[OrderModel]:
    stmt = (
        select(OrderModel)
        .where(OrderModel.user_id == user_id)
        .options(selectinload(OrderModel.order_items))
        .order_by(OrderModel.created_at.desc())
    )
    if status is not None:
        stmt = stmt.where(OrderModel.status == status)
    return list(db.scalars(stmt).all())


def create_order(
    db: Session, 
    order: OrderCreate, 
    user_id: int
) -> OrderModel:
    
    db_order = OrderModel(
        user_id=user_id,
        address=order.address,
        total_price=0.0,
        status="ongoing"
    )

    db.add(db_order)
    db.flush()

    total_price = 0.0
    for cart_item_id in order.cart_items:
        order_item = order_item_repository.create_order_item(db, cart_item_id, db_order.id, commit=False)
        total_price += order_item.price * order_item.quantity

        cart_item = db.get(CartItemModel, cart_item_id)
        if cart_item:
            db.delete(cart_item)

    db_order.total_price = total_price

    try:
        db.commit()
        db.refresh(db_order)
        return db_order
    except Exception as e:
        db.rollback()
        raise e

def complete_order(db: Session, order_id: int, user_id: int) -> Optional[OrderModel]:
    db_order = get_order_by_id(db, order_id, user_id=user_id)
    if not db_order:
        return None

    db_order.status = "completed"

    # Create a gained reward for the user based on the order quantity 
    for order_item in db_order.order_items:
        if(order_item.description != "Redeemed Reward" and order_item.description != "Gachapon Reward"):
            gained_reward = GainedRewardCreate(food_id=order_item.food_id)
            for _ in range(order_item.quantity):
                create_gained_reward(db, gained_reward, user_id)

    try:
        db.commit()
        return get_order_by_id(db, db_order.id, user_id=user_id) or db_order
    except Exception as e:
        db.rollback()
        raise e

def create_order_for_promotion(db: Session, food_id: int, user_id: int, order: OrderCreateNoCart, description: str) -> OrderModel:
    db_order = OrderModel(
        user_id=user_id,
        address=order.address,
        total_price=0.0,
        status="ongoing"
    )

    db.add(db_order)
    db.flush()

    cart_item = CartItemCreateForPromotion(
        food_id=food_id,
        order_id=db_order.id,
        description=description  # You might want to use a more appropriate description here
    )
    order_item = order_item_repository.create_order_items_for_food_id_for_promotion(db, cart_item, commit=False)
    db_order.total_price = order_item.price

    try:
        db.commit()
        db.refresh(db_order)
        return db_order
    except Exception as e:
        db.rollback()
        raise e
