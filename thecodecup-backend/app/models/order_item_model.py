from typing import Optional
from sqlalchemy import String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, validates, relationship
from app.models.base import Base

class OrderItemModel(Base):
    __tablename__ = "ORDER_ITEMS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String)
    description: Mapped[Optional[str]] = mapped_column(String, nullable=True)
    quantity: Mapped[int] = mapped_column()
    price: Mapped[float] = mapped_column()
    order_id: Mapped[int] = mapped_column(ForeignKey("ORDERS.id", ondelete="CASCADE"), index=True)
    food_id: Mapped[int] = mapped_column(ForeignKey("FOODS.id"), index=True)
    
    # Relationship to the OrderModel
    order: Mapped["OrderModel"] = relationship("OrderModel", back_populates="order_items")
    
    # Relationship to the FoodModel
    food: Mapped["FoodModel"] = relationship("FoodModel", back_populates="order_items")