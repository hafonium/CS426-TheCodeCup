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
