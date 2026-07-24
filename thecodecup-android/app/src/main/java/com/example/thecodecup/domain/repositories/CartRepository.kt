package com.example.thecodecup.domain.repositories

import com.example.thecodecup.domain.models.CartItemModel

interface CartRepository {
    suspend fun getCartItems(): Result<List<CartItemModel>>
    suspend fun addCartItem(foodId: Int, quantity: Int, optionTypeIds: List<Int>): Result<Unit>
    suspend fun updateCartItem(cartItemId: Int, foodId: Int, quantity: Int, optionTypeIds: List<Int>): Result<CartItemModel>
    suspend fun updateQuantity(cartItemId: Int, quantity: Int): Result<CartItemModel>
    suspend fun deleteCartItem(cartItemId: Int): Result<List<CartItemModel>>
    suspend fun clearCart(): Result<List<CartItemModel>>
}
