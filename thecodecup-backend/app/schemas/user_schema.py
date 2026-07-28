from pydantic import BaseModel, ConfigDict, EmailStr, Field, field_validator
from typing import Optional
from app.schemas.cart_item_schema import CartItemResponse
import phonenumbers

class UserBase(BaseModel):
    email: EmailStr
    full_name: str
    phone_number: str
    avatar_image_path: Optional[str] = None
    address: str
    model_config = ConfigDict(from_attributes=True)

    @field_validator("phone_number")
    @classmethod
    def validate_phone(cls, v: str) -> str:
        try:
            parsed = phonenumbers.parse(v, "VN")
            if not phonenumbers.is_valid_number(parsed):
                raise ValueError("Invalid phone number format")
            return phonenumbers.format_number(parsed, phonenumbers.PhoneNumberFormat.E164)
        except phonenumbers.NumberParseException:
            raise ValueError("Could not parse phone number")

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
                "password": "",
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

class UserRequestPasswordForgot(BaseModel):
    email: EmailStr
    new_password: str
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "email": "",
                "new_password": ""
            }
        }
    )

class UserUpdatePasswordOTP(BaseModel):
    new_password: str

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "new_password": "new_password"
            }
        }
    )

class UserResponse(UserBase):
    id: int
    cart_items: list[CartItemResponse] = []
    model_config = ConfigDict(from_attributes=True)