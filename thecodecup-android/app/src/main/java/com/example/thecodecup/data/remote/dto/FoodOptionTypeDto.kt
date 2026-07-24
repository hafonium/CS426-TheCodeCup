package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodOptionTypeResponseDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double
)

data class FoodOptionTypeEditResponseDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("is_selected")
    val isSelected: Boolean
)