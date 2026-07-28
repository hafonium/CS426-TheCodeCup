from sqlalchemy.orm import Session
from sqlalchemy import select
from app.models.otp_model import OTPModel
from app.schemas.otp_schema import OTPResponse, OTPCreate
from app.repositories import user_repository
from app.core.config import settings

from datetime import datetime, timedelta
import random

from app.utils.get_vn_time import get_vn_now

def create_otp(db: Session, email: str):
    # Check if an OTP already exists for the user
    stmt = select(OTPModel).where(OTPModel.email == email)
    existing_otp = db.scalars(stmt).first()

    if existing_otp:
        # If an OTP already exists, delete it
        try:
            db.delete(existing_otp)
            db.commit()
        except Exception as e:
            db.rollback()
            raise e

    # Generate a new OTP code (a random 6-digit)
    otp_code = f"{random.randint(100000, 999999)}"

    # Set expiration time 5 minutes from now
    expiration_time = get_vn_now() + timedelta(minutes=5)

    # Create a new OTP entry
    new_otp = OTPModel(
        email=email,
        otp_code=otp_code,
        expiration_time=expiration_time,
        attempt_remaining=settings.MAX_OTP_ATTEMPTS,
        is_successful=False
    )

    try:
        db.add(new_otp)
        db.commit()
        db.refresh(new_otp)
    except Exception as e:
        db.rollback()
        raise e

    return new_otp

def verify_otp(db: Session, email: str, otp_code: str) -> OTPResponse | None:
    stmt = select(OTPModel).where(OTPModel.email == email, OTPModel.expiration_time > get_vn_now())
    otp = db.scalars(stmt).first()
    
    if otp:
        # increase attempt count
        if(otp.attempt_remaining <= 0):
            return OTPResponse(attempt_remaining=otp.attempt_remaining, is_successful=False)
        try:
            otp.attempt_remaining -= 1
            db.commit()
        except Exception as e:
            db.rollback()
            raise e

        if otp.otp_code == otp_code:
            otp.is_successful = True
            # Delete the OTP after successful verification
            try:
                db.delete(otp)
                db.commit()
            except Exception as e:
                db.rollback()
                raise e

            user_repository.verify(db, otp.email)
        return OTPResponse(attempt_remaining=otp.attempt_remaining, is_successful=otp.is_successful)

    return None

def verify_forgot_password_otp(db: Session, email: str, otp_code: str) -> OTPResponse | None:
    stmt = select(OTPModel).where(OTPModel.email == email, OTPModel.expiration_time > get_vn_now())
    otp = db.scalars(stmt).first()
    
    if otp.is_successful:
        return OTPResponse(attempt_remaining=otp.attempt_remaining, is_successful=True)

    if otp:
        # increase attempt count
        if(otp.attempt_remaining <= 0):
            return OTPResponse(attempt_remaining=otp.attempt_remaining, is_successful=False)
        try:
            otp.attempt_remaining -= 1
            db.commit()
        except Exception as e:
            db.rollback()
            raise e

        if otp.otp_code == otp_code:
            otp.is_successful = True
            # Update
            try:
                db.commit()
            except Exception as e:
                db.rollback()
                raise e
        return OTPResponse(attempt_remaining=otp.attempt_remaining, is_successful=otp.is_successful)

    return None

def get_otp_by_email(db: Session, email: str) -> OTPModel | None:
    stmt = select(OTPModel).where(OTPModel.email == email)
    return db.scalars(stmt).first()