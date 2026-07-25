from typing import Optional

from sqlalchemy.orm import Session, selectinload
from sqlalchemy import select

from app.models.order_model import OrderModel
from app.models.cart_item_model import CartItemModel
from app.repositories import order_item_repository
from app.schemas.order_schema import OrderCreate

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
        total_price += order_item.price

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

    try:
        db.commit()
        return get_order_by_id(db, db_order.id, user_id=user_id) or db_order
    except Exception as e:
        db.rollback()
        raise e
