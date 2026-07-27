from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List
from datetime import datetime

from app.schemas.food_schema import FoodResponse

class GainedRewardBase(BaseModel):
    model_config = ConfigDict(from_attributes=True)

class GainedRewardCreate(GainedRewardBase):
    food_id: int

class GainedRewardResponse(GainedRewardBase):
    id: int
    food: FoodResponse
    gained_point: int
    created_at: datetime