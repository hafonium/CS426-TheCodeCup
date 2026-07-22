from sqlalchemy.orm import Session
from sqlalchemy import select, insert
from app.models.food_model import FoodModel
from app.models.food_option_model import FoodOptionModel
from app.schemas.food_schema import FoodCreate, FoodUpdate
from app.schemas.food_option_schema import FoodOptionCreate, FoodOptionUpdate

def get_all_food_options(db: Session) -> list[FoodOptionModel]:
    stmt = select(FoodOptionModel)
    return db.scalars(stmt).all()

def create_food_option(db: Session, food_id: int, food_option: FoodOptionCreate):
    db_food_option = FoodOptionModel(
        food_id=food_id,
        name=food_option.name
    )
    
    try:
        db.add(db_food_option)
        db.commit()
        db.refresh(db_food_option)
        return db_food_option
    except Exception as e:
        db.rollback()
        raise e

def update_food_option_by_id(db: Session, food_option_id: int, food_option: FoodOptionUpdate) -> FoodOptionModel | None:
    stmt = select(FoodOptionModel).where(FoodOptionModel.id == food_option_id)
    db_food_option = db.scalars(stmt).first()
    
    if not db_food_option:
        return None
    
    for key, value in food_option.model_dump(exclude_unset=True).items():
        setattr(db_food_option, key, value)
    
    try:
        db.commit()
        db.refresh(db_food_option)
    except Exception as e:
        db.rollback()
        raise e
    
    return db_food_option

