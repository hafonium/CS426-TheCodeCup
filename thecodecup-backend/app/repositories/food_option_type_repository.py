from sqlalchemy.orm import Session
from sqlalchemy import select
from app.models.food_option_type_model import FoodOptionTypeModel
from app.schemas.food_option_type_schema import FoodOptionTypeCreate, FoodOptionTypeUpdate

def get_all_food_option_types(db: Session) -> list[FoodOptionTypeModel]:
    stmt = select(FoodOptionTypeModel)
    return db.scalars(stmt).all()

def get_food_option_types_by_option_id(db: Session, food_option_id: int) -> list[FoodOptionTypeModel]:
    stmt = select(FoodOptionTypeModel).where(FoodOptionTypeModel.food_option_id == food_option_id)
    return db.scalars(stmt).all()

def create_food_option_type(
    db: Session, 
    option_type_in: FoodOptionTypeCreate, 
    food_option_id: int
) -> FoodOptionTypeModel:
    db_option_type = FoodOptionTypeModel(
        **option_type_in.model_dump(),
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

def update_food_option_type_by_id(
    db: Session, 
    option_type_id: int, 
    option_type_in: FoodOptionTypeUpdate
) -> FoodOptionTypeModel | None:
    stmt = select(FoodOptionTypeModel).where(FoodOptionTypeModel.id == option_type_id)
    db_option_type = db.scalars(stmt).first()
    
    if not db_option_type:
        return None
    
    for key, value in option_type_in.model_dump(exclude_unset=True).items():
        setattr(db_option_type, key, value)
    
    try:
        db.commit()
        db.refresh(db_option_type)
    except Exception as e:
        db.rollback()
        raise e
    
    return db_option_type

def delete_food_option_type_by_id(db: Session, option_type_id: int) -> FoodOptionTypeModel | None:
    stmt = select(FoodOptionTypeModel).where(FoodOptionTypeModel.id == option_type_id)
    db_option_type = db.scalars(stmt).first()
    
    if not db_option_type:
        return None
    
    try:
        db.delete(db_option_type)
        db.commit()
        return db_option_type
    except Exception as e:
        db.rollback()
        raise e