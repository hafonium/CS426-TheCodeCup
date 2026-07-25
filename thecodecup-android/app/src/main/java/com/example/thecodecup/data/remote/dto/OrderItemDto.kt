package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderItemResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price")
    val price: Double,

    @SerializedName("food_id")
    val foodId: Int
)