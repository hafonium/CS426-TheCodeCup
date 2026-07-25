from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import String, Boolean
from app.models.base import Base
from typing import Optional

class FoodModel(Base):
    __tablename__ = "FOODS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String, unique=True, index=True)
    description: Mapped[str] = mapped_column(String)
    price: Mapped[float] = mapped_column()
    image_path: Mapped[Optional[str]] = mapped_column(String, nullable=True)
    category: Mapped[str] = mapped_column(String)
    reward_point: Mapped[Optional[int]] = mapped_column(nullable=True)

    # Relationship to handle child options and support cascade delete
    options: Mapped[list["FoodOptionModel"]] = relationship(
        "FoodOptionModel",
        back_populates="food",
        cascade="all, delete-orphan",
        passive_deletes=True
    )

    # Relationship to the CartItemModel
    cart_items: Mapped[list["CartItemModel"]] = relationship(
        "CartItemModel",
        back_populates="food",
        cascade="all, delete-orphan",
        passive_deletes=True
    )

    order_items: Mapped[list["OrderItemModel"]] = relationship(
        "OrderItemModel",
        back_populates="food",
        cascade="all, delete-orphan",
        passive_deletes=True
    )