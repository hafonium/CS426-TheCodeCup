from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean, ForeignKey
from app.models.base import Base
from typing import Optional

class CartItemModel(Base):
    __tablename__ = "CART_ITEMS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("USERS.id", ondelete="CASCADE"), index=True)
    food_id: Mapped[int] = mapped_column(ForeignKey("FOODS.id", ondelete="CASCADE"), index=True)
    quantity: Mapped[int] = mapped_column()

    # Relationship to the UserModel
    user: Mapped["UserModel"] = relationship("UserModel", back_populates="cart_items")

    # Relationship to the FoodModel
    food: Mapped["FoodModel"] = relationship("FoodModel", back_populates="cart_items")
    # Relationship to the CartItemFoodOptionTypeModel
    option_types: Mapped[list["CartItemFoodOptionTypeModel"]] = relationship(
        "CartItemFoodOptionTypeModel",
        back_populates="cart_item",
        cascade="all, delete-orphan",
        passive_deletes=True
    )