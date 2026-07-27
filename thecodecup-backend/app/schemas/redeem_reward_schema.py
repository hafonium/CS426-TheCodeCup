from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List
from datetime import datetime

from app.schemas.food_schema import FoodResponse

class RedeemRewardBase(BaseModel):
    expiration_time: Optional[datetime] = None
    required_point: int

    model_config = ConfigDict(from_attributes=True)

class RedeemRewardCreate(RedeemRewardBase):
    food_id: int

class RedeemRewardResponse(RedeemRewardBase):
    id: int
    food: FoodResponse

class RedeemRewardUse(BaseModel):
    redeem_reward_id: int
    address: str

    model_config = ConfigDict(from_attributes=True)