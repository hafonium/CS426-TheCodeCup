package com.example.thecodecup.domain.models

data class OrderModel(
    val id: Int,
    val address: String,
    val status: String,
    val totalPrice: Double,
    val createdAt: String,
    val items: List<OrderItemModel>
)

data class OrderItemModel(
    val id: Int,
    val name: String,
    val description: String,
    val quantity: Int,
    val price: Double,
    val foodId: Int
)
