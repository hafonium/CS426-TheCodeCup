from pydantic import BaseModel, ConfigDict, EmailStr, Field
from typing import Optional
from app.schemas.food_option_schema import FoodOptionResponse

class FoodBase(BaseModel):
    name: str
    description: str
    price: float
    image_path: Optional[str] = None
    category: str
    reward_point: Optional[int] = None
    model_config = ConfigDict(from_attributes=True)

class FoodCreate(FoodBase):
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "name": "",
                "description": "",
                "price": 0.0,
                "image_path": None, 
                "category": "",
                "reward_point": None
            }
        }
    )

class FoodResponse(FoodBase):
    id: int
    options: list[FoodOptionResponse] = []
    model_config = ConfigDict(from_attributes=True)

class FoodUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    price: Optional[float] = None
    image_path: Optional[str] = None
    category: Optional[str] = None
    reward_point: Optional[int] = None

    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "example": {
                "name": "Espresso",
                "description": "Rich double shot espresso",
                "price": 3.50,
                "image_path": "/images/espresso.jpg",
                "category": "Coffee",
                "reward_point": 10
            }
        }
    )