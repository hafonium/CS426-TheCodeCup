package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("image_path")
    val imageUrl: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("reward_point")
    val rewardPoint: Int,

    @SerializedName("options")
    val options: List<FoodOptionResponseDto>
)

