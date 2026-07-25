package com.example.thecodecup.domain.usecases.order

import com.example.thecodecup.domain.repositories.OrderRepository

class CompleteOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: Int) = repository.completeOrder(orderId)
}
