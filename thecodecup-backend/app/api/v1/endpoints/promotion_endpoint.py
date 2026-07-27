from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api.deps import get_db, get_current_user
from app.models.user_model import UserModel

from app.repositories import promotion_repository
from app.schemas.promotion_schema import PromotionResponse
from app.schemas.gacha_schema import GachaResponse, GachaUse
from app.schemas.redeem_reward_schema import RedeemRewardUse
from app.schemas.gained_reward_schema import GainedRewardResponse

router = APIRouter(prefix="/promotions", tags=["Promotions"])

@router.get("", response_model=PromotionResponse, status_code=status.HTTP_200_OK)
def get_user_promotion(
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    promotion = promotion_repository.get_promotion_by_user_id(db, user_id=current_user.id)
    if not promotion:
        raise HTTPException(status_code=404, detail="Promotion not found")
    return promotion

@router.get("/gained-rewards", response_model=list[GainedRewardResponse], status_code=status.HTTP_200_OK)
def list_gained_rewards(
    current_user: UserModel = Depends(get_current_user), 
    db: Session = Depends(get_db)
) -> list[GainedRewardResponse]:
    from app.repositories.gained_reward_repository import get_all_gained_rewards

    gained_rewards = get_all_gained_rewards(db, current_user.id)
    return gained_rewards

@router.post("/reward-point", response_model=PromotionResponse, status_code=status.HTTP_200_OK)
def use_reward_points(
    redeem_reward_use: RedeemRewardUse,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    try:
        updated_promotion = promotion_repository.use_reward_points(db, user_id=current_user.id, redeem_reward_use=redeem_reward_use)
        return updated_promotion
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.post("/gachapon", response_model=GachaResponse, status_code=status.HTTP_200_OK)
def use_gachapon(
    gacha_use: GachaUse,
    current_user: UserModel = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    try:
        gacha_response = promotion_repository.use_gachapon(
            db, 
            user_id=current_user.id,
            gacha_use=gacha_use
        )
        return gacha_response
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))