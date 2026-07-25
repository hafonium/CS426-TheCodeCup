from typing import List
from sqlalchemy.orm import Session
from sqlalchemy import select
from app.models.food_option_type_model import FoodOptionTypeModel
from app.schemas.food_option_type_schema import FoodOptionTypeCreate


def get_food_option_types_by_option_id(db: Session, food_option_id: int) -> List[FoodOptionTypeModel]:
    """
    Fetches all option types belonging to a specific food option group.
    """
    stmt = (
        select(FoodOptionTypeModel)
        .where(FoodOptionTypeModel.food_option_id == food_option_id)
        .order_by(FoodOptionTypeModel.price.asc())
    )
    return list(db.scalars(stmt).all())


def create_food_option_type(
    db: Session, 
    option_type_in: FoodOptionTypeCreate, 
    food_option_id: int
) -> FoodOptionTypeModel:
    """
    Creates a new food option type. Ensures dynamic UI fields 
    (such as `is_selected`) are excluded prior to model instantiation.
    """
    # Dump payload data and safely strip dynamic UI fields if present
    data = option_type_in.model_dump(exclude_unset=True)
    data.pop("is_selected", None)
    
    db_option_type = FoodOptionTypeModel(
        **data,
        food_option_id=food_option_id
    )
    
    try:
        db.add(db_option_type)
        db.commit()
        db.refresh(db_option_type)
        return db_option_type
    except Exception as e:
        db.rollback()
        raise e