from datetime import timedelta
from typing import Annotated
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm, OAuth2PasswordBearer
from sqlalchemy.orm import Session

from app.api.deps import get_db, get_current_user
from app.core.security import verify_password, create_access_token, ACCESS_TOKEN_EXPIRE_MINUTES
from app.repositories import user_repository
from app.schemas.token_schema import Token
from app.models.user_model import UserModel
import jwt
from app.core.security import SECRET_KEY, ALGORITHM
from app.repositories import revoked_token_repository
from datetime import datetime, timezone
from app.core.exceptions import EmailVerificationException

router = APIRouter(prefix="/auth", tags=["auth"])
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="api/v1/auth/login")

@router.post("/login", response_model=Token)
def login_for_access_token(
    # Note: OAuth2PasswordRequestForm expects a 'username' field in the form.
    # In frontend/Swagger, we will type the user's email into the "username" box.
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db)
):
    user = user_repository.get_user_by_email(db, email=form_data.username)
    
    # Check if user exists AND password is correct
    if not user or not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
        
    if not user.is_verified:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={
                "code": "EMAIL_NOT_VERIFIED",
                "message": "Email exists but is not verified. Please verify your email before logging in."
            }
        )
            
    # Generate the JWT token
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": str(user.id), "token_version": user.token_version},
        expires_delta=access_token_expires
    )
    
    return Token(access_token=access_token, token_type="bearer")

@router.post("/logout", status_code=status.HTTP_200_OK)
def logout(
    current_user: UserModel = Depends(get_current_user), 
    token: str = Depends(oauth2_scheme),
    db: Session = Depends(get_db),
):
    payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    exp_timestamp = payload.get("exp")
    expires_at = datetime.fromtimestamp(exp_timestamp, tz=timezone.utc).replace(tzinfo=None)
    
    revoked_token_repository.add_revoked_token(db, token=token, expires_at=expires_at)

    return {"message": "Successfully logged out"}