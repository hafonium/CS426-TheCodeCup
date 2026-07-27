from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List
from datetime import datetime
from app.schemas.food_schema import FoodResponse
from app.schemas.promotion_schema import PromotionResponse

class GachaBase(BaseModel):
    promotion: PromotionResponse
    food: FoodResponse
    model_config = ConfigDict(from_attributes=True)

class GachaResponse(GachaBase):
    pass

class GachaUse(BaseModel):
    address: str
    model_config = ConfigDict(from_attributes=True)