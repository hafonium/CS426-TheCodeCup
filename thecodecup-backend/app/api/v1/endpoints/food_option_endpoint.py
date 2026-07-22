from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api.deps import get_db
from app.schemas.food_option_type_schema import FoodOptionTypeCreate, FoodOptionTypeResponse
from app.repositories import food_option_type_repository

router = APIRouter(prefix="/food-options", tags=["Food Options"])

@router.post(
    "/{food_option_id}/types", 
    response_model=FoodOptionTypeResponse, 
    status_code=status.HTTP_201_CREATED
)
def create_type_for_option(
    food_option_id: int,
    type_in: FoodOptionTypeCreate,
    db: Session = Depends(get_db)
):
    return food_option_type_repository.create_food_option_type(
        db=db, 
        option_type_in=type_in, 
        food_option_id=food_option_id
    )