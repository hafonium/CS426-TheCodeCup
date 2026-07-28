from sqlalchemy.orm import Session
from sqlalchemy import select
from app.models.user_model import UserModel
from app.schemas.user_schema import UserCreate, UserUpdate
from app.schemas.promotion_schema import PromotionCreate
from app.core.security import get_password_hash, verify_password
from app.core.exceptions import EmailAlreadyExistsException, PasswordMismatchException, EmailVerificationException
from app.repositories.promotion_repository import create_promotion
from app.repositories.otp_repository import get_otp_by_email

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
    user_response = get_user_by_email(db, user.email)
    if user_response:
        if not user_response.is_verified:
            raise EmailVerificationException()
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

        create_promotion(db, promotion=PromotionCreate(user_id=db_user.id))
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
        
def verify(db, email: str):
    db_user = get_user_by_email(db, email)
    if not db_user:
        raise ValueError("User not found")
    
    db_user.is_verified = True

    try:
        db.commit()
        db.refresh(db_user)
    except Exception as e:
        db.rollback()
        raise e

def update_password_forgot(db: Session, email: str, new_password: str):
    db_user = get_user_by_email(db, email)
    if not db_user:
        raise ValueError("User not found")

    otp = get_otp_by_email(db, email)
    if not otp or otp.is_successful is False:
        raise ValueError("OTP verification required before updating password")

    db_user.hashed_password = get_password_hash(new_password)

    try:
        db.commit()
        db.refresh(db_user)
    except Exception as e:
        db.rollback()
        raise e
