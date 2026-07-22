from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.schemas.user_schema import UserCreate, UserResponse, UserUpdate
from starlette import status
from app.repositories import user_repository
from app.api.deps import get_db, get_current_user
from app.models.user_model import UserModel
from app.core.exceptions import EmailAlreadyExistsException, PasswordMismatchException

router = APIRouter(prefix="/users", tags=["users"])

@router.get("", response_model=list[UserResponse], status_code=status.HTTP_200_OK)
def list_users(db: Session = Depends(get_db)) -> list[UserResponse]:
    return user_repository.get_all_users(db)

@router.get("/me", response_model=UserResponse, status_code=status.HTTP_200_OK)
def get_my_profile(current_user: UserModel = Depends(get_current_user)) -> UserResponse:
    return current_user

@router.get("/{user_id}", response_model=UserResponse, status_code=status.HTTP_200_OK)
def get_user(user_id: int, db: Session = Depends(get_db)) -> UserResponse:
    db_user = user_repository.get_user_by_id(db, user_id)
    if not db_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return db_user

@router.post("", status_code=status.HTTP_201_CREATED)
def create_user(user: UserCreate, db: Session = Depends(get_db)):
    try:
        user_repository.create_user(db, user)
    except EmailAlreadyExistsException as e:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=str(e))

@router.put("/me", status_code=status.HTTP_200_OK)
def update_user(
    userUpdate: UserUpdate, 
    current_user: UserModel = Depends(get_current_user), 
    db: Session = Depends(get_db)):
    try:
        db_user = user_repository.update_user(db, current_user, userUpdate)
        return db_user
    except EmailAlreadyExistsException as e:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=str(e))
    except PasswordMismatchException as e:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=str(e))
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))