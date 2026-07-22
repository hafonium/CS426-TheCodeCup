from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Float, ForeignKey
from app.models.base import Base

class FoodOptionTypeModel(Base):
    __tablename__ = "FOOD_OPTION_TYPES"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    # Foreign key referencing parent FOOD_OPTIONS table
    food_option_id: Mapped[int] = mapped_column(
        ForeignKey("FOOD_OPTIONS.id", ondelete="CASCADE"), 
        index=True
    )
    name: Mapped[str] = mapped_column(String)  # e.g., "Medium", "Large"
    price: Mapped[float] = mapped_column(Float, default=0.0)  # e.g., 0.50

    # Relationship back to parent FoodOptionModel
    option: Mapped["FoodOptionModel"] = relationship(
        "FoodOptionModel", 
        back_populates="option_types"
    )