from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey, DateTime
from app.models.base import Base
from typing import Optional
from datetime import datetime

class RedeemRewardModel(Base):
    __tablename__ = "REDEEM_REWARDS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    food_id: Mapped[int] = mapped_column(ForeignKey("FOODS.id"), nullable=False)
    expiration_time: Mapped[datetime] = mapped_column(
        DateTime(timezone=False), # Naive timestamp
        index=True,
        nullable=False
    )
    required_point: Mapped[int] = mapped_column(nullable=False)

    # Relationship to the FoodModel
    food: Mapped["FoodModel"] = relationship("FoodModel", back_populates="redeem_rewards")