from sqlalchemy.orm import Session, joinedload, selectinload
from sqlalchemy import select
from app.models.order_item_model import OrderItemModel
from app.models.cart_item_food_option_type_model import CartItemFoodOptionTypeModel
from app.models.cart_item_model import CartItemModel
from app.repositories import cart_item_repository

def get_order_items_by_order_id(db: Session, order_id: int):
    """
    Fetches all order items associated with a specific order.
    Pre-loads associated food data for detailed order view.
    """
    stmt = (
        select(OrderItemModel)
        .where(OrderItemModel.order_id == order_id)
        .options(joinedload(OrderItemModel.food))
        .order_by(OrderItemModel.id.asc())
    )
    return list(db.scalars(stmt).unique().all())

def create_order_item(db: Session, cart_item_id: int, order_id: int, commit: bool = True):
    """
    Creates a new order item based on a cart item and associates it with the specified order.
    """
    # Fetch the cart item to be converted into an order item
    cart_item = cart_item_repository.get_cart_item_by_id(db, cart_item_id)
    if not cart_item:
        raise ValueError(f"Cart item with ID {cart_item_id} not found.")
    
    # Create a new OrderItemModel instance based on the cart item
    order_item = OrderItemModel(
        name=cart_item.food.name,
        food_id=cart_item.food_id,
        quantity=cart_item.quantity,
        # (Food base price + option prices) * quantity
        price=(cart_item.food.price + sum(opt.option_type.price for opt in cart_item.option_types if opt.option_type is not None)),
        order_id=order_id,
        # Combine all options into a single description string for the order item 
        description=", ".join([opt.option_type.name for opt in cart_item.option_types if opt.option_type is not None])
    )
    
    
    try: 
        db.add(order_item)
        if commit:
            db.commit()
            db.refresh(order_item)
        else:
            db.flush()
        return order_item
    except Exception as e:
        db.rollback()
        raise e