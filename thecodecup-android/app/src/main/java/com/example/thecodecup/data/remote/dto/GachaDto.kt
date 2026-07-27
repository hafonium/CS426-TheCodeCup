package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GachaResponseDto(
    @SerializedName("promotion")
    val promotion: PromotionResponseDto,

    @SerializedName("food")
    val food: FoodResponseDto
)

data class GachaUseDto(
    @SerializedName("address")
    val address: String
)