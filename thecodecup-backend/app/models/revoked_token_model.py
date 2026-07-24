from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import String, DateTime
from datetime import datetime
from app.models.base import Base

class RevokedTokenModel(Base):
     __tablename__ = "REVOKED_TOKENS"
 
     id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
     token: Mapped[str] = mapped_column(String, unique=True)
     expires_at: Mapped[datetime] = mapped_column(DateTime, nullable=False)