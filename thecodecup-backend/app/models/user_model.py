from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import String, Boolean
from app.models.base import Base
from pydantic import field_validator
import phonenumbers

class UserModel(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    email: Mapped[str] = mapped_column(String, unique=True, index=True)
    hashed_password: Mapped[str] = mapped_column(String)
    full_name: Mapped[str] = mapped_column(String)
    phone_number: Mapped[str] = mapped_column(String)
    avatar_res_id: Mapped[str] = mapped_column(String, nullable=True)
    token_version: Mapped[int] = mapped_column(default=0)


    @field_validator("phone_number")
    @classmethod
    def validate_phone_number(cls, value: str) -> str:
        try:
            parsed_number = phonenumbers.parse(value, "VN") 
            
            if not phonenumbers.is_valid_number(parsed_number):
                raise ValueError("Invalid phone number format")
                
            return phonenumbers.format_number(parsed_number, phonenumbers.PhoneNumberFormat.E164)
            
        except phonenumbers.NumberParseException:
            raise ValueError("Could not parse phone number")