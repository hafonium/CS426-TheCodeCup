from typing import Optional
from sqlalchemy import String, CheckConstraint, DateTime, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, validates, relationship
from app.models.base import Base
from app.utils.get_vn_time import get_vn_now
from datetime import datetime

class PromotionModel(Base):
    __tablename__ = "PROMOTIONS"

    user_id: Mapped[int] = mapped_column(ForeignKey("USERS.id", ondelete="CASCADE"), primary_key=True, index=True)
    total_reward_point: Mapped[int] = mapped_column(nullable=False, default=0)
    loyalty_count: Mapped[int] = mapped_column(nullable=False, default=0)
    gachapon_count: Mapped[int] = mapped_column(nullable=False, default=0)