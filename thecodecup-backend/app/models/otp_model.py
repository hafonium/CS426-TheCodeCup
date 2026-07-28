from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey, DateTime
from app.models.base import Base
from typing import Optional
from datetime import datetime
from app.core.config import settings

class OTPModel(Base):
    __tablename__ = "OTPS"

    email: Mapped[str] = mapped_column(ForeignKey("USERS.email", ondelete="CASCADE"), primary_key=True, index=True)
    otp_code: Mapped[str] = mapped_column(String(6), nullable=False)
    expiration_time: Mapped[datetime] = mapped_column(
        DateTime(timezone=False), # Naive timestamp
    )
    attempt_remaining: Mapped[int] = mapped_column(default=settings.MAX_OTP_ATTEMPTS)
    is_successful: Mapped[bool] = mapped_column(Boolean, default=False)

    # Relationship to the UserModel
    user: Mapped["UserModel"] = relationship("UserModel", back_populates="otp")