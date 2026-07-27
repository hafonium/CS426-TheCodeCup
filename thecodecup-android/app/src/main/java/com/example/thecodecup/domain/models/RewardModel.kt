package com.example.thecodecup.domain.models

data class PromotionModel(
    val totalRewardPoint: Int,
    val loyaltyCount: Int,
    val gachaponCount: Int
)

data class GainedRewardModel(
    val id: Int,
    val food: FoodModel,
    val gainedPoint: Int,
    val createdAt: String
)

data class RedeemRewardModel(
    val id: Int,
    val food: FoodModel,
    val expirationTime: String,
    val requiredPoint: Int
)

data class GachaResultModel(
    val promotion: PromotionModel,
    val food: FoodModel
)
