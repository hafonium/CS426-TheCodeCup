import jwt
from typing import Annotated
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jwt.exceptions import InvalidTokenError
from sqlalchemy.orm import Session

from app.core.security import SECRET_KEY, ALGORITHM
from app.schemas.token_schema import TokenData
from app.repository import user_repository, revoked_token_repository
from app.models.user_model import UserModel
from app.core.database import SessionLocal


oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v1/auth/login")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def get_current_user(
    token: Annotated[str, Depends(oauth2_scheme)],
    db: Session = Depends(get_db)
) -> UserModel:
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    
    try:
        # Check if the token is revoked
        revoked_token = revoked_token_repository.get_revoked_token_by_token(db, token)
        if revoked_token:
            raise credentials_exception
            
        # Decode the JWT token
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id: int = int(payload.get("sub"))
        token_version: int = int(payload.get("token_version"))
        if user_id is None:
            raise credentials_exception
        token_data = TokenData(id=user_id, token_version=token_version)
    except InvalidTokenError:
        raise credentials_exception
        
    # Fetch the real user from SQLAlchemy database
    user = user_repository.get_user_by_id(db, user_id=token_data.id)
    if user is None:
        raise credentials_exception

    # Check if the token version matches
    if user.token_version != token_data.token_version:
        raise credentials_exception
        
    return user