package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RedeemRewardResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("food")
    val food: FoodResponseDto,

    @SerializedName("expiration_time")
    val expirationTime: String,

    @SerializedName("required_point")
    val requiredPoint: Int
)

data class RedeemRewardCreateDto(
    @SerializedName("food_id")
    val foodId: Int,

    @SerializedName("expiration_time")
    val expirationTime: String,

    @SerializedName("required_point")
    val requiredPoint: Int
)

data class RedeemRewardUseDto(
    @SerializedName("address")
    val address: String,

    @SerializedName("redeem_reward_id")
    val redeemRewardId: Int
)