package com.example.thecodecup.domain.usecases.cart

import com.example.thecodecup.domain.repositories.CartRepository

class DeleteCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: Int) = repository.deleteCartItem(cartItemId)
}
