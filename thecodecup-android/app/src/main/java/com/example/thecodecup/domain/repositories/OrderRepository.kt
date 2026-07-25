package com.example.thecodecup.domain.repositories

import com.example.thecodecup.domain.models.OrderModel

interface OrderRepository {
    suspend fun createOrder(address: String, cartItemIds: List<Int>): Result<OrderModel>
    suspend fun getOrders(status: String): Result<List<OrderModel>>
    suspend fun completeOrder(orderId: Int): Result<OrderModel>
}
