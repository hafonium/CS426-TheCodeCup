from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session, selectinload

from app.models.redeem_reward_model import RedeemRewardModel
from app.schemas.redeem_reward_schema import RedeemRewardCreate, RedeemRewardResponse

from app.utils.get_vn_time import get_vn_now

def get_all_redeem_rewards(
    db: Session,
    limit: Optional[int] = None,
    offset: int = 0,
) -> List[RedeemRewardModel]:
    today = get_vn_now()  # Ensure the current time is fetched for comparison
    stmt = (
        select(RedeemRewardModel)
        .where(RedeemRewardModel.expiration_time > today)
        .options(selectinload(RedeemRewardModel.food))
        .order_by(RedeemRewardModel.required_point.asc())
    )
    if offset:
        stmt = stmt.offset(offset)
    if limit is not None:
        stmt = stmt.limit(limit)
    return list(db.scalars(stmt).all())

def get_redeem_reward_by_id(db: Session, redeem_reward_id: int) -> Optional[RedeemRewardModel]:
    today = get_vn_now()
    stmt = (
        select(RedeemRewardModel)
        .where(RedeemRewardModel.id == redeem_reward_id)
        .where(RedeemRewardModel.expiration_time > today)
        .options(selectinload(RedeemRewardModel.food))
    )
    return db.scalars(stmt).first()
    
def create_redeem_reward(
    db: Session, 
    redeem_reward: RedeemRewardCreate) -> RedeemRewardModel:

    db_redeem_reward = RedeemRewardModel(
        food_id=redeem_reward.food_id,
        expiration_time=redeem_reward.expiration_time,
        required_point=redeem_reward.required_point
    )
    
    try:
        db.add(db_redeem_reward)
        db.commit()
        db.refresh(db_redeem_reward)
        return db_redeem_reward
    except Exception as e:
        db.rollback()
        raise e

