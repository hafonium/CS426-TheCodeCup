from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List
from app.schemas.food_schema import FoodResponse
from app.schemas.food_option_type_schema import FoodOptionTypeResponse
from app.schemas.food_option_schema import FoodOptionEditResponse

class CartItemBase(BaseModel):
    quantity: int
    model_config = ConfigDict(from_attributes=True)

class CartItemCreate(CartItemBase):
    food_id: int
    option_type_ids: List[int] = []  

class CartItemUpdate(CartItemBase):
    option_type_ids: List[int] = []  # Optional list of option type IDs to update
    
class CartItemResponse(BaseModel): 
    id: int
    quantity: int
    food: FoodResponse
    option_types: List[FoodOptionTypeResponse] = []

    @field_validator("option_types", mode="before")
    @classmethod
    def extract_option_types(cls, v):
        if isinstance(v, list) and v and hasattr(v[0], "option_type"):
            return [item.option_type for item in v if item.option_type is not None]
        return v


    model_config = ConfigDict(from_attributes=True)

class CartItemEditResponse(BaseModel):
    id: int
    quantity: int
    food: FoodResponse
    options: List[FoodOptionEditResponse] = []  

    model_config = ConfigDict(from_attributes=True)

class CartItemCreateForPromotion(BaseModel):
    food_id: int
    order_id: int
    description: str