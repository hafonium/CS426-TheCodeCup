package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.CartItemCreateDto
import com.example.thecodecup.data.remote.dto.CartItemEditResponseDto
import com.example.thecodecup.data.remote.dto.CartItemResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CartApiService {
    @GET("cart")
    suspend fun getCartItems(
        @Header("Authorization") token: String,
    ): List<CartItemResponseDto>

    @POST("cart/items")
    suspend fun addCartItem(
        @Body cartItemInfo: CartItemCreateDto,
        @Header("Authorization") token: String,
    )

    @GET("cart/items/{cartItemId}/edit")
    suspend fun getCartItemForEdit(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("cartItemId") cartItemId: Int
    ): CartItemEditResponseDto

    @PUT("cart/items/{cartItemId}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("cartItemId") cartItemId: Int,
        @Body cartItemInfo: CartItemCreateDto
    ): CartItemResponseDto

    @PATCH("cart/items/{cartItemId}/quantity")
    suspend fun updateCartItemQuantity(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("cartItemId") cartItemId: Int,
        @retrofit2.http.Query("quantity") quantity: Int
    ): CartItemResponseDto

    @DELETE("cart/items/{cartItemId}")
    suspend fun deleteCartItem(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("cartItemId") cartItemId: Int
    ): List<CartItemResponseDto>

    @DELETE("cart")
    suspend fun clearCart(
        @Header("Authorization") token: String
    ): List<CartItemResponseDto>
}
