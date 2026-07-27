from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload, selectinload

from app.models.promotion_model import PromotionModel
from app.schemas.promotion_schema import PromotionCreate, PromotionResponse
from app.repositories.redeem_reward_repository import get_redeem_reward_by_id
from app.schemas.gacha_schema import GachaResponse, GachaUse
from app.repositories.food_repository import get_random_food
from app.schemas.order_schema import OrderCreateNoCart
from app.schemas.redeem_reward_schema import RedeemRewardUse

def get_promotion_by_user_id(db: Session, user_id: int) -> PromotionModel:
    stmt = (
        select(PromotionModel)
        .where(PromotionModel.user_id == user_id)
    )
    return db.scalars(stmt).first()

def create_promotion(
    db: Session, 
    promotion: PromotionCreate) -> PromotionModel:

    db_promotion = PromotionModel(
        user_id=promotion.user_id,
        total_reward_point=0,
        loyalty_count=0,
        gachapon_count=0
    )
    
    try:
        db.add(db_promotion)
        db.commit()
        db.refresh(db_promotion)
        return db_promotion
    except Exception as e:
        db.rollback()
        raise e

def increase_total_reward_point(db: Session, user_id: int, points: int, commit: bool = True) -> PromotionModel:
    promotion = get_promotion_by_user_id(db, user_id)
    if not promotion:
        raise ValueError(f"Promotion for user_id {user_id} does not exist.")

    try: 
        promotion.total_reward_point += points
        if commit:
            db.commit()
            db.refresh(promotion)
        else:
            db.flush()
        return promotion
    except Exception as e:
        db.rollback()
        raise e

def use_reward_points(db: Session, user_id: int, redeem_reward_use: RedeemRewardUse) -> PromotionModel:
    # Get Redeem Reward ID from RedeemRewardUse
    redeem_reward_id = redeem_reward_use.redeem_reward_id
    redeem_reward = get_redeem_reward_by_id(db, redeem_reward_id)
    if not redeem_reward:
        raise ValueError(f"Redeem reward with id {redeem_reward_id} does not exist.")

    # Check if user has enough points
    user_promotion = get_promotion_by_user_id(db, user_id)
    if not user_promotion or user_promotion.total_reward_point < redeem_reward.required_point:
        raise ValueError("Insufficient reward points")

    # Deduct points and create order
    user_promotion.total_reward_point -= redeem_reward.required_point
    try: 
        from app.repositories.order_repository import create_order_for_promotion

        create_order_for_promotion(
            db,
            redeem_reward.food_id,
            user_id,
            OrderCreateNoCart(address=redeem_reward_use.address),
            description="Redeemed Reward"
        )
        db.commit()
        db.refresh(user_promotion)
        return user_promotion
    except Exception as e:
        db.rollback()
        raise e


def increase_loyalty_count(db: Session, user_id: int, commit: bool = True) -> PromotionModel:
    promotion = get_promotion_by_user_id(db, user_id)
    if not promotion:
        raise ValueError(f"Promotion for user_id {user_id} does not exist.")

    try:
        promotion.loyalty_count += 1
        if(promotion.loyalty_count >= 8):
            promotion.loyalty_count = 0
            promotion.gachapon_count += 1

        if commit:
            db.commit()
            db.refresh(promotion)
        else:
            db.flush()
        return promotion
    except Exception as e:
        db.rollback()
        raise e

def use_gachapon(db: Session, user_id: int, gacha_use: GachaUse) -> GachaResponse:
    user_promotion = get_promotion_by_user_id(db, user_id)
    if not user_promotion or user_promotion.gachapon_count <= 0:
        raise ValueError("No gachapon available to use.")

    try:
        # Decrease the gachapon count
        user_promotion.gachapon_count -= 1
        food_item = get_random_food(db)
        if not food_item:
            raise ValueError("No food available for gachapon reward.")

        # Place order for the random food item
        from app.repositories.order_repository import create_order_for_promotion

        create_order_for_promotion(
            db,
            food_item.id,
            user_id,
            OrderCreateNoCart(address=gacha_use.address),
            description="Gachapon Reward"
        )

        db.commit()
        db.refresh(user_promotion)

        return GachaResponse(promotion=user_promotion, food=food_item)
    except Exception as e:
        db.rollback()
        raise e