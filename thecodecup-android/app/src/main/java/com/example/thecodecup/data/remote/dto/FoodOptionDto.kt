package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodOptionResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("option_types")
    val foodOptionTypes: List<FoodOptionTypeResponseDto>
)

data class FoodOptionEditResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("option_types")
    val foodOptionTypes: List<FoodOptionTypeEditResponseDto>
)