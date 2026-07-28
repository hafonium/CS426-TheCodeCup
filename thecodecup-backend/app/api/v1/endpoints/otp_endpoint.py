from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.services import otp_service
from app.api.deps import get_db, get_current_user
from app.models.user_model import UserModel
from app.schemas.otp_schema import OTPResponse, OTPCreate, OTPVerification
from app.repositories import otp_repository
from fastapi import BackgroundTasks

router = APIRouter(prefix="/otp", tags=["OTP"])

@router.post("/send", status_code=200)
def send_otp(
    otp: OTPCreate, background_tasks: BackgroundTasks, db: Session = Depends(get_db)
):
  otp = otp_repository.create_otp(db, otp.email)
  if not otp:
    raise HTTPException(status_code=400, detail="Could not generate OTP")

  # Queue the email sending to happen in the background
  background_tasks.add_task(
      otp_service.send_otp_email, otp.otp_code, otp.email
  )

  return {"message": "OTP generated and sending in background"}

@router.post("/verify-email", response_model=OTPResponse, status_code=200)
def verify_email(otp: OTPVerification, db: Session = Depends(get_db)):
    """
    Endpoint to verify the OTP provided by the user.
    """
    otp_response = otp_repository.verify_otp(db, otp.email, otp.otp_code)
    if otp_response is None:
        raise HTTPException(status_code=400, detail="Invalid, expired OTP or incorrect email.")
    elif not otp_response.is_successful:
        raise HTTPException(status_code=400, detail=f"OTP verification failed. Attempt remaining: {otp_response.attempt_remaining}")

    return otp_response

@router.post("/verify-forgot-password", response_model=OTPResponse, status_code=200)
def verify_forgot_password_otp(otp: OTPVerification,db: Session = Depends(get_db)):
    """
    Endpoint to verify the OTP for password forgot functionality.
    """
    otp_response = otp_repository.verify_forgot_password_otp(db, otp.email, otp.otp_code)
    if otp_response is None:
        raise HTTPException(status_code=400, detail="Invalid, expired OTP or incorrect email.")
    elif not otp_response.is_successful:
        raise HTTPException(status_code=400, detail=f"OTP verification failed. Attempt remaining: {otp_response.attempt_remaining}")

    return otp_response
