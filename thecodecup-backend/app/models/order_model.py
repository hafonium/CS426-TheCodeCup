from typing import Optional
from sqlalchemy import String, CheckConstraint, DateTime, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, validates, relationship
from app.models.base import Base
from app.utils.get_vn_time import get_vn_now
from datetime import datetime

class OrderModel(Base):
    __tablename__ = "ORDERS"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("USERS.id", ondelete="CASCADE"), index=True)
    total_price: Mapped[float] = mapped_column()
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=False), # Naive timestamp
        default=get_vn_now
    )
    status: Mapped[str] = mapped_column(String, default="ongoing")  # Default status is "ongoing"
    address: Mapped[str] = mapped_column(String)
    
    # Add a check constraint to ensure that the status is one of the allowed values 
    __table_args__ = (
        CheckConstraint(
            status.in_(["ongoing", "completed"]),
            name="check_status_valid"
        ),
    )

    # Relationship to the UserModel
    user: Mapped["UserModel"] = relationship("UserModel", back_populates="orders")

    # Relationship to the OrderItemModel
    order_items: Mapped[list["OrderItemModel"]] = relationship(
        "OrderItemModel",
        back_populates="order",
        cascade="all, delete-orphan",
        passive_deletes=True
    )

