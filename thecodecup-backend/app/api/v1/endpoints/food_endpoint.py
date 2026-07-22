from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from starlette import status
from app.repositories import food_option_repository
from app.repositories import food_repository
from app.schemas.food_option_schema import FoodOptionCreate, FoodOptionResponse, FoodOptionUpdate
from app.schemas.food_schema import FoodResponse, FoodCreate, FoodUpdate
from app.api.deps import get_db, get_current_user

router = APIRouter(prefix="/foods", tags=["foods"])

@router.get("", response_model=list[FoodResponse], status_code=status.HTTP_200_OK)
def list_foods(db: Session = Depends(get_db)) -> list[FoodResponse]:
    return food_repository.get_all_food(db)

@router.get("/{food_id}", response_model=FoodResponse, status_code=status.HTTP_200_OK)
def get_food(food_id: int, db: Session = Depends(get_db)) -> FoodResponse:
    db_food = food_repository.get_food_by_id(db, food_id)
    if not db_food:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Food not found")
    return db_food

@router.post("", response_model=FoodResponse, status_code=status.HTTP_201_CREATED)
def create_food(food: FoodCreate, db: Session = Depends(get_db)) -> FoodResponse:
    db_food = food_repository.create_food(db, food)
    return db_food

@router.put("/{food_id}", response_model=FoodResponse, status_code=status.HTTP_200_OK)
def update_food(food_id: int, food: FoodUpdate, db: Session = Depends(get_db)) -> FoodResponse:
    db_food = food_repository.update_food_by_id(db, food_id, food)
    if not db_food:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Food not found")
    return db_food

@router.delete("/{food_id}", response_model=FoodResponse, status_code=status.HTTP_200_OK)
def delete_food(food_id: int, db: Session = Depends(get_db)) -> FoodResponse:
    db_food = food_repository.delete_food_by_id(db, food_id)
    if not db_food:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Food not found")
    return db_food

@router.post("/{food_id}/options", response_model=FoodOptionResponse, status_code=status.HTTP_201_CREATED)
def create_food_option(food_id: int, food_option: FoodOptionCreate, db: Session = Depends(get_db)) -> FoodOptionResponse:
    db_food = food_repository.get_food_by_id(db, food_id)
    if not db_food:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Food not found")
    
    db_food_option = food_option_repository.create_food_option(db, food_id, food_option)
    return db_food_option

@router.put("/{food_id}/options/{option_id}", response_model=FoodOptionResponse, status_code=status.HTTP_200_OK)
def update_food_option(food_id: int, option_id: int, food_option: FoodOptionUpdate, db: Session = Depends(get_db)) -> FoodOptionResponse:
    db_food = food_repository.get_food_by_id(db, food_id)
    if not db_food:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Food not found")
    
    db_food_option = food_option_repository.update_food_option(db, food_id, option_id, food_option)
    if not db_food_option:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Food option not found")
    
    return db_food_option