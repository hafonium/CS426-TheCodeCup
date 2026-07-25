from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List
from datetime import datetime
from app.schemas.order_item_schema import OrderItemResponse

class OrderBase(BaseModel):
    address: str
    model_config = ConfigDict(from_attributes=True)

class OrderCreate(OrderBase):
    cart_items: List[int] = []  # List of cart item ids associated with the order


class OrderResponse(OrderBase):
    id: int
    status: str
    total_price: float
    created_at: datetime
    order_items: List[OrderItemResponse] = []  # List of cart items associated with the order

    model_config = ConfigDict(from_attributes=True)