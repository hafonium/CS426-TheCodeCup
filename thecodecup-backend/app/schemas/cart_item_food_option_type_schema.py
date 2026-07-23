from pydantic import BaseModel, ConfigDict, EmailStr, Field
from typing import Optional

class CartItemFoodOptionTypeBase(BaseModel):
    model_config = ConfigDict(from_attributes=True)

class CartItemFoodOptionTypeResponse(CartItemFoodOptionTypeBase):
    id: int
    cart_item_id: int
    food_option_type_id: int
    
    model_config = ConfigDict(from_attributes=True)