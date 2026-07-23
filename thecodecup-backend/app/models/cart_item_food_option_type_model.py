from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey
from app.models.base import Base

class CartItemFoodOptionTypeModel(Base):
    __tablename__ = "CART_ITEM_FOOD_OPTION_TYPES"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    cart_item_id: Mapped[int] = mapped_column(ForeignKey("CART_ITEMS.id", ondelete="CASCADE"), index=True)
    food_option_type_id: Mapped[int] = mapped_column(ForeignKey("FOOD_OPTION_TYPES.id", ondelete="CASCADE"), index=True)

    # Relationship to the CartItemModel
    cart_item: Mapped["CartItemModel"] = relationship(
        "CartItemModel", 
        back_populates="option_types"
    )

    # Relationship to the FoodOptionTypeModel
    option_type: Mapped["FoodOptionTypeModel"] = relationship(
        "FoodOptionTypeModel", 
        back_populates="cart_item_options"
    )