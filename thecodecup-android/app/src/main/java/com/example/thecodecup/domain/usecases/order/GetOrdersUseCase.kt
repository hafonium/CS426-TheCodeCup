package com.example.thecodecup.domain.usecases.order

import com.example.thecodecup.domain.repositories.OrderRepository

class GetOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(status: String) = repository.getOrders(status)
}
