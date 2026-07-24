package com.example.thecodecup.domain.usecases.cart

import com.example.thecodecup.domain.repositories.CartRepository

class AddCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(foodId: Int, quantity: Int, optionTypeIds: List<Int>) =
        repository.addCartItem(foodId, quantity, optionTypeIds)
}
