from pydantic import BaseModel, ConfigDict, EmailStr, Field
from typing import Optional
from app.schemas.food_option_type_schema import FoodOptionTypeResponse

class FoodOptionBase(BaseModel):
    name: str
    model_config = ConfigDict(from_attributes=True)

class FoodOptionCreate(FoodOptionBase):
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "name": "Extra Cheese"
            }
        }
    )

class FoodOptionResponse(FoodOptionBase):
    option_types: list[FoodOptionTypeResponse] = [] 
    model_config = ConfigDict(from_attributes=True)

class FoodOptionUpdate(BaseModel):
    name: Optional[str] = None
    
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "example": {
                "name": "Extra Cheese"
            }
        }
    )