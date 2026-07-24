package com.example.thecodecup.domain.usecases.cart

import com.example.thecodecup.domain.repositories.CartRepository

class UpdateCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: Int, foodId: Int, quantity: Int, optionTypeIds: List<Int>) =
        repository.updateCartItem(cartItemId, foodId, quantity, optionTypeIds)
}
