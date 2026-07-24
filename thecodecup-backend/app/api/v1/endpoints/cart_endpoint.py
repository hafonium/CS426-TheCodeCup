from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api.deps import get_db, get_current_user
from app.models.user_model import UserModel
from app.repositories import cart_item_repository
from app.schemas.cart_item_schema import (
    CartItemCreate, 
    CartItemResponse, 
    CartItemEditResponse,
    CartItemUpdate
)

router = APIRouter(prefix="/cart", tags=["Cart"])

@router.get("", response_model=list[CartItemResponse])
def get_my_cart(
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return cart_item_repository.get_all_cart_items(db, user_id=current_user.id)

@router.post("/items", status_code=status.HTTP_201_CREATED)
def add_to_cart(
    cart_item_in: CartItemCreate,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return cart_item_repository.create_cart_item(
        db=db, 
        cart_item_in=cart_item_in, 
        user_id=current_user.id, 
        food_id=cart_item_in.food_id
    )

@router.get("/items/{cart_item_id}/edit", response_model=CartItemEditResponse)
def get_cart_item_for_edit(
    cart_item_id: int,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    edit_data = cart_item_repository.get_cart_item_for_edit(db, cart_item_id)
    if not edit_data:
        raise HTTPException(status_code=404, detail="Cart item not found")
    return edit_data

@router.put("/items/{cart_item_id}", response_model=CartItemResponse, status_code=status.HTTP_200_OK)
def update_cart_item(
    cart_item_id: int,
    cart_item_in: CartItemUpdate,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Update quantity and selected options for a specific cart item.
    Returns the updated cart item object.
    """
    updated_item = cart_item_repository.update_cart_item(
        db=db,
        cart_item_id=cart_item_id,
        quantity=cart_item_in.quantity,
        option_type_ids=cart_item_in.option_type_ids
    )
    if not updated_item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail="Cart item not found"
        )
    return updated_item


@router.patch("/items/{cart_item_id}/quantity", response_model=CartItemResponse, status_code=status.HTTP_200_OK)
def update_cart_item_quantity(
    cart_item_id: int,
    quantity: int,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Quick update for quantity only (e.g., clicking '+' or '-' buttons on UI).
    Returns the updated cart item object.
    """
    if quantity <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Quantity must be greater than 0"
        )
        
    updated_item = cart_item_repository.update_quantity_by_id(
        db=db,
        cart_item_id=cart_item_id,
        quantity=quantity
    )
    if not updated_item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail="Cart item not found"
        )
    return updated_item

@router.delete("/items/{cart_item_id}", status_code=status.HTTP_200_OK, response_model=list[CartItemResponse])
def delete_cart_item(
    cart_item_id: int,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Remove a single item from the cart.
    """
    deleted = cart_item_repository.delete_cart_item_by_id(
        db=db, 
        cart_item_id=cart_item_id
    )
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail="Cart item not found"
        )
    # Return the new list of cart items after deletion
    return cart_item_repository.get_all_cart_items(db, user_id=current_user.id)


@router.delete("", status_code=status.HTTP_200_OK, response_model=list[CartItemResponse])
def clear_cart(
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Clear all items in the user's cart (e.g., after placing an order).
    """
    cart_item_repository.clear_user_cart(db=db, user_id=current_user.id)
    return cart_item_repository.get_all_cart_items(db, user_id=current_user.id)