from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey
from app.models.base import Base

class FoodOptionTypeModel(Base):
    __tablename__ = "FOOD_OPTION_TYPES"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String)
    price: Mapped[float] = mapped_column()
    food_option_id: Mapped[int] = mapped_column(ForeignKey("FOOD_OPTIONS.id"), index=True)

    # Relationship to the FoodOptionModel
    option: Mapped["FoodOptionModel"] = relationship(
        "FoodOptionModel", 
        back_populates="option_types"
    )

    # Relationship to the CartItemFoodOptionTypeModel
    cart_item_options: Mapped[list["CartItemFoodOptionTypeModel"]] = relationship(
        "CartItemFoodOptionTypeModel", 
        back_populates="option_type",
        cascade="all, delete-orphan",
        passive_deletes=True
    )   