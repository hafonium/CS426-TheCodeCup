from sqlalchemy.orm import Session
from sqlalchemy import select, insert
from app.models.user_model import UserModel
from app.schemas.user_schema import UserCreate, UserUpdate
from app.core.security import get_password_hash, verify_password
from app.core.exceptions import EmailAlreadyExistsException, PasswordMismatchException

def get_all_users(db: Session) -> list[UserModel]:
    stmt = select(UserModel)
    return db.scalars(stmt).all()

def get_user_by_id(db: Session, user_id: int) -> UserModel | None:
    stmt = select(UserModel).where(UserModel.id == user_id)
    return db.scalars(stmt).first()

def get_user_by_email(db: Session, email: str) -> UserModel | None:
    stmt = select(UserModel).where(UserModel.email == email)
    return db.scalars(stmt).first()
    
def create_user(db: Session, user: UserCreate):
    if get_user_by_email(db, user.email):
        raise EmailAlreadyExistsException(email=user.email)

    hashed_password = get_password_hash(user.password) 
    db_user = UserModel(
        email=user.email,
        hashed_password=hashed_password,
        full_name=user.full_name,
        phone_number=user.phone_number,
        avatar_image_path=user.avatar_image_path,
        address=user.address
    )
    
    try:
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
    except Exception as e:
        db.rollback()
        raise e

def update_user(db: Session, current_user: UserModel, user: UserUpdate): 
    db_user = current_user
    if not db_user:
        raise ValueError("User not found")

    # Update email
    if(user.email and user.email != db_user.email and get_user_by_email(db, user.email)):
        raise EmailAlreadyExistsException(email=user.email)

    # Check old password if new password is provided
    if user.new_password:
        if not db_user.hashed_password or not verify_password(user.old_password, db_user.hashed_password):
            raise PasswordMismatchException()

    # Update fields
    if user.email and user.email != db_user.email:
        db_user.email = user.email

    if user.full_name:
        db_user.full_name = user.full_name

    if user.phone_number:
        db_user.phone_number = user.phone_number

    if user.avatar_image_path:
        db_user.avatar_image_path = user.avatar_image_path

    if user.address:
        db_user.address = user.address

    if user.new_password:
        db_user.hashed_password = get_password_hash(user.new_password)

    try:
        db.commit()
        db.refresh(db_user)
    except Exception as e:
        db.rollback()
        raise e
        
    