from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload, selectinload

from app.models.redeem_reward_model import RedeemRewardModel
from app.schemas.redeem_reward_schema import RedeemRewardCreate, RedeemRewardResponse

from app.utils.get_vn_time import get_vn_now

def get_all_redeem_rewards(db: Session) -> List[RedeemRewardModel]:
    stmt = (
        select(RedeemRewardModel)
        .where(RedeemRewardModel.expiration_time > get_vn_now())
        .options(selectinload(RedeemRewardModel.food))
        .order_by(RedeemRewardModel.expiration_time.asc())
    )
    return list(db.scalars(stmt).unique().all())

def get_redeem_reward_by_id(db: Session, redeem_reward_id: int) -> Optional[RedeemRewardModel]:
    stmt = (
        select(RedeemRewardModel)
        .where(RedeemRewardModel.id == redeem_reward_id)
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

