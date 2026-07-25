package com.example.thecodecup.domain.usecases.order

import com.example.thecodecup.domain.repositories.OrderRepository

class CreateOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(address: String, cartItemIds: List<Int>) =
        repository.createOrder(address.trim(), cartItemIds)
}
