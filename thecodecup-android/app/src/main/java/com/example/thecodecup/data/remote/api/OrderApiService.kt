package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.OrderCreateDto
import com.example.thecodecup.data.remote.dto.OrderResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApiService {
    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String
    ): List<OrderResponseDto>

    @GET("orders/{orderId}")
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Int
    ): OrderResponseDto

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body orderInfo: OrderCreateDto
    ): OrderResponseDto

    @PATCH("orders/{orderId}/complete")
    suspend fun completeOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Int
    ): OrderResponseDto
}