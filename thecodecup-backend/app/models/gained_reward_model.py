from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey, DateTime
from app.models.base import Base
from typing import Optional
from datetime import datetime

class GainedRewardModel(Base):
    __tablename__ = "GAINED_REWARDS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("USERS.id"), nullable=False)
    food_id: Mapped[int] = mapped_column(ForeignKey("FOODS.id"), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=False), # Naive timestamp
    )
    gained_point: Mapped[int] = mapped_column(nullable=False)

    # Relationship to the UserModel
    user: Mapped["UserModel"] = relationship(
        "UserModel", back_populates="gained_rewards" 
    )
    # Relationship to the FoodModel
    food: Mapped["FoodModel"] = relationship(
        "FoodModel",
        back_populates="redeem_rewards",  
    )

    food: Mapped["FoodModel"] = relationship(
        "FoodModel", back_populates="gained_rewards"  
    )