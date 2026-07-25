from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload, selectinload

from app.models.cart_item_model import CartItemModel
from app.models.cart_item_food_option_type_model import CartItemFoodOptionTypeModel
from app.models.food_model import FoodModel
from app.models.food_option_model import FoodOptionModel
from app.schemas.cart_item_schema import CartItemCreate, CartItemEditResponse
from app.schemas.food_option_schema import FoodOptionEditResponse
from app.schemas.food_option_type_schema import FoodOptionTypeEditResponse


def get_all_cart_items(db: Session, user_id: int) -> List[CartItemModel]:
    """
    Retrieves all cart items belonging to a specific user.
    Pre-loads associated food and option type data for cart listing.
    """
    stmt = (
        select(CartItemModel)
        .where(CartItemModel.user_id == user_id)
        .options(
            joinedload(CartItemModel.food),
            selectinload(CartItemModel.option_types).joinedload(CartItemFoodOptionTypeModel.option_type)
        )
    )
    return list(db.scalars(stmt).unique().all())

def get_cart_item_by_id(db: Session, cart_item_id: int) -> Optional[CartItemModel]:
    """
    Retrieves a specific cart item by its ID.
    Pre-loads associated food and option type data for detailed view.
    """
    stmt = (
        select(CartItemModel)
        .where(CartItemModel.id == cart_item_id)
        .options(
            joinedload(CartItemModel.food),
            selectinload(CartItemModel.option_types).joinedload(CartItemFoodOptionTypeModel.option_type)
        )
    )
    return db.scalars(stmt).first()
    
def get_cart_item_for_edit(db: Session, cart_item_id: int) -> Optional[CartItemEditResponse]:
    """
    Retrieves a cart item along with the food's full option menu.
    Dynamically computes the `is_selected` flag for each option type 
    based on the user's current cart selection.
    """
    # 1. Fetch CartItem along with currently selected option_type IDs
    stmt = (
        select(CartItemModel)
        .where(CartItemModel.id == cart_item_id)
        .options(
            joinedload(CartItemModel.food),
            selectinload(CartItemModel.option_types)
        )
    )
    cart_item = db.scalars(stmt).first()
    if not cart_item:
        return None

    # Collect selected option type IDs into a set for O(1) lookup
    selected_type_ids = {assoc.food_option_type_id for assoc in cart_item.option_types}

    # 2. Fetch the full option menu for the associated food
    stmt_options = (
        select(FoodOptionModel)
        .where(FoodOptionModel.food_id == cart_item.food_id)
        .options(selectinload(FoodOptionModel.option_types))
    )
    food_options = db.scalars(stmt_options).all()

    # 3. Dynamically map options and calculate the `is_selected` flag
    options_response = []
    for option_group in food_options:
        types_response = []
        for opt_type in option_group.option_types:
            types_response.append(
                FoodOptionTypeEditResponse(
                    id=opt_type.id,
                    name=opt_type.name,
                    price=opt_type.price,
                    # Evaluates to True if this option is currently selected in the cart
                    is_selected=(opt_type.id in selected_type_ids)
                )
            )
        
        options_response.append(
            FoodOptionEditResponse(
                id=option_group.id,
                name=option_group.name,
                option_types=types_response
            )
        )

    # 4. Return the constructed response DTO
    return CartItemEditResponse(
        id=cart_item.id,
        quantity=cart_item.quantity,
        food=cart_item.food,
        options=options_response
    )


def create_cart_item(
    db: Session, 
    cart_item_in: CartItemCreate, 
    user_id: int,
    food_id: int
) -> CartItemModel:
    """
    Creates a new cart item and associates selected option types if provided.
    """
    db_cart_item = CartItemModel(
        quantity=cart_item_in.quantity,
        user_id=user_id,
        food_id=food_id
    )
    
    # Attach selected option types to the relationship if present in payload
    if hasattr(cart_item_in, "option_type_ids") and cart_item_in.option_type_ids:
        for opt_id in cart_item_in.option_type_ids:
            assoc = CartItemFoodOptionTypeModel(food_option_type_id=opt_id)
            db_cart_item.option_types.append(assoc)
    
    try:
        db.add(db_cart_item)
        db.commit()
        db.refresh(db_cart_item)
        return db_cart_item
    except Exception as e:
        db.rollback()
        raise e


def update_cart_item(
    db: Session,
    cart_item_id: int,
    quantity: int,
    option_type_ids: List[int]
) -> Optional[CartItemModel]:
    """
    Updates the quantity and replaces all selected option types for a cart item.
    Uses orphan removal to clean up unlinked junction records automatically.
    """
    stmt = (
        select(CartItemModel)
        .where(CartItemModel.id == cart_item_id)
        .options(selectinload(CartItemModel.option_types))
    )
    db_cart_item = db.scalars(stmt).first()
    
    if not db_cart_item:
        return None
    
    db_cart_item.quantity = quantity
    
    # Clear existing options; SQLAlchemy's delete-orphan handles DB deletion
    db_cart_item.option_types.clear()
    
    # Attach newly selected option types
    for opt_id in option_type_ids:
        assoc = CartItemFoodOptionTypeModel(food_option_type_id=opt_id)
        db_cart_item.option_types.append(assoc)
        
    try:
        db.commit()
        db.refresh(db_cart_item)
        return db_cart_item
    except Exception as e:
        db.rollback()
        raise e


def update_quantity_by_id(
    db: Session, 
    cart_item_id: int, 
    quantity: int
) -> Optional[CartItemModel]:
    """
    Updates only the quantity field of a cart item (e.g., +/- buttons on UI).
    """
    stmt = select(CartItemModel).where(CartItemModel.id == cart_item_id)
    db_cart_item = db.scalars(stmt).first()
    
    if not db_cart_item:
        return None
    
    db_cart_item.quantity = quantity
    
    try:
        db.commit()
        db.refresh(db_cart_item)
        return db_cart_item
    except Exception as e:
        db.rollback()
        raise e

def clear_user_cart(db: Session, user_id: int) -> None:
    """
    Deletes all cart items for a specific user.
    """
    stmt = select(CartItemModel).where(CartItemModel.user_id == user_id)
    cart_items = db.scalars(stmt).all()
    
    for item in cart_items:
        db.delete(item)
    
    try:
        db.commit()
    except Exception as e:
        db.rollback()
        raise e

def delete_cart_item_by_id(db: Session, cart_item_id: int) -> bool:
    """
    Deletes a specific cart item by its ID.
    Returns True if deletion was successful, False if the item was not found.
    """
    stmt = select(CartItemModel).where(CartItemModel.id == cart_item_id)
    db_cart_item = db.scalars(stmt).first()
    
    if not db_cart_item:
        return False
    
    db.delete(db_cart_item)
    
    try:
        db.commit()
        return True
    except Exception as e:
        db.rollback()
        raise e