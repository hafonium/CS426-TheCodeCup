package com.example.thecodecup.domain.usecases.cart

import com.example.thecodecup.domain.repositories.CartRepository

class GetCartItemsUseCase(private val repository: CartRepository) {
    suspend operator fun invoke() = repository.getCartItems()
}
