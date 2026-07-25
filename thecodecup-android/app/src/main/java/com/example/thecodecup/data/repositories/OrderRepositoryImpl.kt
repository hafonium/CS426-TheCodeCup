package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.remote.dto.OrderCreateDto
import com.example.thecodecup.data.remote.dto.OrderResponseDto
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.OrderItemModel
import com.example.thecodecup.domain.models.OrderModel
import com.example.thecodecup.domain.repositories.OrderRepository
import com.example.thecodecup.utils.getHttpMessage
import retrofit2.HttpException

class OrderRepositoryImpl(private val authPreferences: AuthPreferences) : OrderRepository {
    private val api = ApiClient.orderApiService

    override suspend fun createOrder(address: String, cartItemIds: List<Int>) =
        call { api.createOrder(token(), OrderCreateDto(address, cartItemIds)).toDomain() }

    override suspend fun getOrders(status: String) =
        call { api.getOrders(token(), status).map { it.toDomain() } }

    override suspend fun completeOrder(orderId: Int) =
        call { api.completeOrder(token(), orderId).toDomain() }

    private fun token() =
        "Bearer ${authPreferences.getAuthToken() ?: throw IllegalStateException("Please sign in again")}"

    private suspend fun <T> call(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: HttpException) {
        Result.failure(Exception(getHttpMessage(e)))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Unable to load orders"))
    }

    private fun OrderResponseDto.toDomain() = OrderModel(
        id = id,
        address = address,
        status = status,
        totalPrice = totalPrice,
        createdAt = createdAt,
        items = orderItems.map {
            OrderItemModel(it.id, it.name, it.description, it.quantity, it.price, it.foodId)
        }
    )
}
