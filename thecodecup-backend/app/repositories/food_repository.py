from sqlalchemy.orm import Session, selectinload
from sqlalchemy import select
from app.models.food_model import FoodModel
from app.models.food_option_model import FoodOptionModel
from app.schemas.food_schema import FoodCreate, FoodUpdate

def get_all_food(db: Session, category: str = None) -> list[FoodModel]:
    stmt = select(FoodModel)
    
    if category:
        stmt = stmt.where(FoodModel.category == category)
        
    stmt = stmt.options(
        selectinload(FoodModel.options).selectinload(FoodOptionModel.option_types)
    )
    return list(db.scalars(stmt).all())

def get_food_by_id(db: Session, food_id: int) -> FoodModel | None:
    stmt = (
        select(FoodModel)
        .where(FoodModel.id == food_id)
        .options(
            selectinload(FoodModel.options).selectinload(FoodOptionModel.option_types)
        )
    )
    return db.scalars(stmt).first()

def create_food(db: Session, food: FoodCreate) -> FoodModel:
    db_food = FoodModel(**food.model_dump())
    
    try:
        db.add(db_food)
        db.commit()
        db.refresh(db_food)
        return db_food
    except Exception as e:
        db.rollback()
        raise e

def update_food_by_id(db: Session, food_id: int, food: FoodUpdate) -> FoodModel | None:
    stmt = select(FoodModel).where(FoodModel.id == food_id)
    db_food = db.scalars(stmt).first()
    
    if not db_food:
        return None
    
    for key, value in food.model_dump(exclude_unset=True).items():
        setattr(db_food, key, value)
    
    try:
        db.commit()
        db.refresh(db_food)
    except Exception as e:
        db.rollback()
        raise e
    
    return db_food

def delete_food_by_id(db: Session, food_id: int) -> FoodModel | None:
    stmt = select(FoodModel).where(FoodModel.id == food_id)
    db_food = db.scalars(stmt).first()
    
    if not db_food:
        return None
    
    try:
        db.delete(db_food)
        db.commit()
    except Exception as e:
        db.rollback()
        raise e
    
    return db_food

def get_food_options_by_food_id(db: Session, food_id: int) -> list[FoodOptionModel]:
    stmt = select(FoodOptionModel).where(FoodOptionModel.food_id == food_id)
    return db.scalars(stmt).all()