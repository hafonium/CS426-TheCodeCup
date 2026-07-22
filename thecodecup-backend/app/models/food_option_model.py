from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey
from app.models.base import Base

class FoodOptionModel(Base):
    __tablename__ = "FOOD_OPTIONS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    food_id: Mapped[int] = mapped_column(
        ForeignKey("FOODS.id", ondelete="CASCADE"), 
        index=True
    )
    name: Mapped[str] = mapped_column(String)

    # Relationship back to parent food model
    food: Mapped["FoodModel"] = relationship("FoodModel", back_populates="options")
    option_types: Mapped[list["FoodOptionTypeModel"]] = relationship("FoodOptionTypeModel", back_populates="option")