from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List

class OrderItemBase(BaseModel):
    model_config = ConfigDict(from_attributes=True)

class OrderItemCreate(OrderItemBase):
    pass 

class OrderItemResponse(OrderItemBase):
    id: int
    name: str 
    description: str
    quantity: int
    price: float
    food_id: int


    model_config = ConfigDict(from_attributes=True)