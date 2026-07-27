package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PromotionResponseDto(
    @SerializedName("total_reward_point")
    val totalRewardPoint: Int,

    @SerializedName("loyalty_count")
    val loyaltyCount: Int,

    @SerializedName("gachapon_count")
    val gachaponCount: Int,
)
