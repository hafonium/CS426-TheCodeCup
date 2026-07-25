package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import java.io.Serial

data class OrderResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("address") val address: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("order_items") val orderItems: List<OrderItemResponseDto>
)

data class OrderCreateDto(
    @SerializedName("address") val address: String,
    @SerializedName("cart_items") val cartItems: List<Int> // list of cart item ids
)