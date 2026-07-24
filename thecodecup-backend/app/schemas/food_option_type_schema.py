from typing import Optional
from pydantic import BaseModel, ConfigDict

class FoodOptionTypeBase(BaseModel):
    name: str
    price: float = 0.0
    model_config = ConfigDict(from_attributes=True)

class FoodOptionTypeCreate(FoodOptionTypeBase):
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "name": "Medium",
                "price": 0.50
            }
        }
    )

class FoodOptionTypeResponse(FoodOptionTypeBase):
    id: int
    model_config = ConfigDict(from_attributes=True)

class FoodOptionTypeEditResponse(FoodOptionTypeResponse):
    is_selected: bool = False

class FoodOptionTypeUpdate(BaseModel):
    name: Optional[str] = None
    price: Optional[float] = None
    
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "example": {
                "name": "Large",
                "price": 1.00
            }
        }
    )