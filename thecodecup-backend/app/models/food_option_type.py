from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean
from app.models.base import Base
from pydantic import field_validator

class FoodOptionTypeModel(Base):
    __tablename__ = "FOOD_OPTION_TYPES"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String, unique=True)
    price: Mapped[float] = mapped_column()
    food_option_id: Mapped[int] = mapped_column(ForeignKey("FOOD_OPTIONS.id"), index=True)