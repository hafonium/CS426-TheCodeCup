from pydantic import BaseModel, ConfigDict, computed_field, field_validator
from typing import Optional, List
from datetime import datetime

class PromotionBase(BaseModel):
    model_config = ConfigDict(from_attributes=True)

class PromotionCreate(PromotionBase):
    user_id: int

class PromotionResponse(PromotionBase):
    total_reward_point: int
    loyalty_count: int
    gachapon_count: int

