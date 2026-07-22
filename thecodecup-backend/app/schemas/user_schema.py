from pydantic import BaseModel, ConfigDict, EmailStr, Field
from typing import Optional

class UserBase(BaseModel):
    email: EmailStr
    full_name: str
    phone_number: str
    avatar_image_path: Optional[str] = None
    address: str
    model_config = ConfigDict(from_attributes=True)

class UserCreate(UserBase):
    password: str
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "email": "",
                "full_name": "",
                "phone_number": "",
                "avatar_image_path": None, 
                "address": "",
                "password": ""
            }
        }
    )

class UserUpdate(BaseModel):
    email: Optional[EmailStr] = None
    full_name: Optional[str] = None
    phone_number: Optional[str] = None
    avatar_image_path: Optional[str] = None
    address: Optional[str] = None
    old_password: Optional[str] = None
    new_password: Optional[str] = None
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "email": None,
                "full_name": None,
                "phone_number": None,
                "avatar_image_path": None, 
                "address": None,
                "old_password": None,
                "new_password": None
            }
        }
    )

class UserResponse(UserBase):
    id: int
    model_config = ConfigDict(from_attributes=True)