from sqlalchemy.orm import Session
from sqlalchemy import select, insert
from app.models.user_model import UserModel
from app.schemas.user_schema import UserCreate
from app.core.security import get_password_hash
from app.core.exceptions import EmailAlreadyExistsException

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
        avatar_res_id=user.avatar_res_id
    )
    
    try:
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
    except Exception as e:
        db.rollback()
        raise e
    