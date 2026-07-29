from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload, selectinload

from app.models.gained_reward_model import GainedRewardModel
from app.schemas.gained_reward_schema import GainedRewardCreate, GainedRewardResponse
from app.models.food_model import FoodModel
from app.repositories.food_repository import get_food_by_id

from app.utils.get_vn_time import get_vn_now

def get_all_gained_rewards(
    db: Session, 
    user_id: int,
    limit: Optional[int] = None,
    offset: int = 0
) -> List[GainedRewardModel]:

    stmt = (
        select(GainedRewardModel)
        .where(GainedRewardModel.user_id == user_id)
        .options(selectinload(GainedRewardModel.food))
        .order_by(GainedRewardModel.created_at.desc())
    )

    if offset:
        stmt = stmt.offset(offset)
    if limit is not None:
        stmt = stmt.limit(limit)
    return list(db.scalars(stmt).all())

def create_gained_reward(
    db: Session, 
    gained_reward: GainedRewardCreate, 
    user_id: int) -> GainedRewardModel:

    food_db = get_food_by_id(db, gained_reward.food_id)
    if not food_db:
        raise ValueError(f"Food with id {gained_reward.food_id} does not exist.")

    db_gained_reward = GainedRewardModel(
        user_id=user_id,
        food_id=gained_reward.food_id,
        created_at=get_vn_now(),
        gained_point=food_db.reward_point if food_db.reward_point is not None else 0  
    )
    
    try:
        db.add(db_gained_reward)

        from app.repositories.promotion_repository import (
            increase_total_reward_point,
            increase_loyalty_count,
        )

        increase_total_reward_point(db, user_id, db_gained_reward.gained_point, commit=False)
        increase_loyalty_count(db, user_id, commit=False)
        db.commit()
        db.refresh(db_gained_reward)
        
        return db_gained_reward
    except Exception as e:
        db.rollback()
        raise e