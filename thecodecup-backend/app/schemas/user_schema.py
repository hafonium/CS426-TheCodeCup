from pydantic import BaseModel, ConfigDict, EmailStr, Field

class UserBase(BaseModel):
    email: EmailStr
    full_name: str
    phone_number: str
    avatar_image_path: str | None = None
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

class UserUpdate(UserBase):
    email: EmailStr | None = None
    full_name: str | None = None
    phone_number: str | None = None
    avatar_image_path: str | None = None
    address: str | None = None
    old_password: str | None = None
    new_password: str | None = None
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