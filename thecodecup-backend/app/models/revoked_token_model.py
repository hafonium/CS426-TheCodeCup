from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import String, DateTime
from datetime import datetime
from app.models.base import Base
from pydantic import field_validator

class RevokedTokenModel(Base):
     __tablename__ = "revoked_tokens"
 
     id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
     token: Mapped[str] = mapped_column(String, unique=True)
     expires_at: Mapped[datetime] = mapped_column(DateTime, nullable=False)