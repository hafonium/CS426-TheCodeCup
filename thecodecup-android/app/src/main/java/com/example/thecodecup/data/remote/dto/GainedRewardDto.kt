package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GainedRewardResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("food")
    val food: FoodResponseDto,

    @SerializedName("gained_point")
    val gainedPoint: Int,

    @SerializedName("created_at")
    val createdAt: String
)