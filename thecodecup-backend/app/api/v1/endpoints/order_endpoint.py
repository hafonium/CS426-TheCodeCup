from typing import Literal

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.api.deps import get_db, get_current_user
from app.models.user_model import UserModel
from app.repositories import cart_item_repository, order_repository
from app.schemas.order_schema import OrderCreate, OrderResponse

router = APIRouter(prefix="/orders", tags=["Orders"])


@router.post("", response_model=OrderResponse, status_code=status.HTTP_201_CREATED)
def place_order(
    order_in: OrderCreate, 
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return order_repository.create_order(db=db, order=order_in, user_id=current_user.id)


@router.get("", response_model=list[OrderResponse])
def get_my_orders(
    status: Literal["ongoing", "completed"] = Query(default="ongoing", description="Filter orders by status"),
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return order_repository.get_all_orders(db, user_id=current_user.id, status=status)

@router.get("/{order_id}", response_model=OrderResponse)
def get_order_by_id(
    order_id: int,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    order = order_repository.get_order_by_id(db, order_id=order_id, user_id=current_user.id)
    if not order:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Order not found")
    return order    

@router.patch("/{order_id}/complete", response_model=OrderResponse, status_code=status.HTTP_200_OK)
def complete_order(
    order_id: int,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    updated_order = order_repository.complete_order(db, order_id=order_id, user_id=current_user.id)
    if not updated_order:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Order not found")
    return updated_order