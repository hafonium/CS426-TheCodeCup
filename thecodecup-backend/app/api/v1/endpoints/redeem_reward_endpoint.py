from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api.deps import get_db, get_current_user
from app.models.user_model import UserModel

from app.repositories import redeem_reward_repository
from app.schemas.redeem_reward_schema import RedeemRewardCreate, RedeemRewardResponse

router = APIRouter(prefix="/redeem-rewards", tags=["Redeem Rewards"])

@router.get("", response_model=list[RedeemRewardResponse], status_code=status.HTTP_200_OK)
def get_redeem_rewards(
    db: Session = Depends(get_db),
    limit: int | None = None,
    offset: int = 0,
):
    return redeem_reward_repository.get_all_redeem_rewards(db, limit=limit, offset=offset)

@router.get("/{redeem_reward_id}", response_model=RedeemRewardResponse, status_code=status.HTTP_200_OK)
def get_redeem_reward_by_id(
    redeem_reward_id: int,
    db: Session = Depends(get_db)
):
    redeem_reward = redeem_reward_repository.get_redeem_reward_by_id(db, redeem_reward_id)
    if not redeem_reward:
        raise HTTPException(status_code=404, detail="Redeem reward not found")
    return redeem_reward

@router.post("", response_model=RedeemRewardResponse, status_code=status.HTTP_201_CREATED)
def create_redeem_reward(
    redeem_reward_in: RedeemRewardCreate,
    db: Session = Depends(get_db)
):
    return redeem_reward_repository.create_redeem_reward(db, redeem_reward_in)
