from pydantic import BaseModel, ConfigDict, EmailStr, Field
from typing import Optional
from datetime import datetime

class OTPBase(BaseModel):
    model_config = ConfigDict(from_attributes=True)

class OTPVerification(OTPBase):
    email: EmailStr
    otp_code: str
    
class OTPCreate(OTPBase):
    email: EmailStr

class OTPResponse(OTPBase):
    attempt_remaining: int
    is_successful: bool = Field(default=False)
    model_config = ConfigDict(from_attributes=True)