from typing import Optional
from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column, validates, relationship
from app.models.base import Base
import phonenumbers

class UserModel(Base):
    __tablename__ = "USERS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    email: Mapped[str] = mapped_column(String, unique=True, index=True)
    hashed_password: Mapped[str] = mapped_column(String)
    full_name: Mapped[str] = mapped_column(String)
    phone_number: Mapped[str] = mapped_column(String)
    
    avatar_image_path: Mapped[Optional[str]] = mapped_column(String, nullable=True)
    
    address: Mapped[str] = mapped_column(String)
    token_version: Mapped[int] = mapped_column(default=0)

    cart_items: Mapped[list["CartItemModel"]] = relationship(
        "CartItemModel",
        back_populates="user",
        cascade="all, delete-orphan",
        passive_deletes=True
    )

    @validates("phone_number")
    def validate_phone_number(self, key: str, value: str) -> str:
        if not value:
            raise ValueError("Phone number cannot be empty")
            
        try:
            parsed_number = phonenumbers.parse(value, "VN") 
            
            if not phonenumbers.is_valid_number(parsed_number):
                raise ValueError("Invalid phone number format")
                
            return phonenumbers.format_number(
                parsed_number, 
                phonenumbers.PhoneNumberFormat.E164
            )
            
        except phonenumbers.NumberParseException:
            raise ValueError("Could not parse phone number")