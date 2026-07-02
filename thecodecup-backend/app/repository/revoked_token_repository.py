from sqlalchemy.orm import Session
from sqlalchemy import select, insert
from app.models.revoked_token_model import RevokedTokenModel

def get_all_revoked_tokens(db: Session):
    return db.execute(select(RevokedTokenModel)).scalars().all()

def add_revoked_token(db: Session, token: str, expires_at):
    revoked_token = RevokedTokenModel(token=token, expires_at=expires_at)
    db.add(revoked_token)
    db.commit()

def get_revoked_token_by_token(db: Session, token: str):
    smt = select(RevokedTokenModel).where(RevokedTokenModel.token == token)
    return db.execute(smt).scalar_one_or_none()