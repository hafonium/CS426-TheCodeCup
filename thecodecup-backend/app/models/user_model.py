from typing import Optional
from sqlalchemy import String, Boolean, DateTime
from sqlalchemy.orm import Mapped, mapped_column, validates, relationship
from app.models.base import Base
from typing import List
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
    is_verified: Mapped[bool] = mapped_column(Boolean, default=False)

    cart_items: Mapped[list["CartItemModel"]] = relationship(
        "CartItemModel",
        back_populates="user",
        cascade="all, delete-orphan",
        passive_deletes=True
    )

    orders: Mapped[list["OrderModel"]] = relationship(
        "OrderModel",
        back_populates="user",
        cascade="all, delete-orphan",
        passive_deletes=True
    )

    gained_rewards: Mapped[List["GainedRewardModel"]] = relationship(
        "GainedRewardModel",  
        back_populates="user",  
    )

    otp: Mapped["OTPModel"] = relationship(
        "OTPModel",
        back_populates="user",
        cascade="all, delete-orphan",
        passive_deletes=True
    )