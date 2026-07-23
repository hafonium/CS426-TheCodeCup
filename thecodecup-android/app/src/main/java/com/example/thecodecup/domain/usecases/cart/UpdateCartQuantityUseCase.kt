package com.example.thecodecup.domain.usecases.cart

import com.example.thecodecup.domain.repositories.CartRepository

class UpdateCartQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: Int, quantity: Int) =
        repository.updateQuantity(cartItemId, quantity)
}
