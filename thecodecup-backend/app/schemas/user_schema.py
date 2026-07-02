from pydantic import BaseModel, ConfigDict, EmailStr, Field

class UserBase(BaseModel):
    email: EmailStr
    full_name: str
    phone_number: str
    avatar_res_id: str | None = None
    model_config = ConfigDict(from_attributes=True)

class UserCreate(UserBase):
    password: str
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "email": "",
                "full_name": "",
                "phone_number": "",
                "avatar_res_id": None, 
                "password": ""
            }
        }
    )

class UserResponse(UserBase):
    id: int
    model_config = ConfigDict(from_attributes=True)