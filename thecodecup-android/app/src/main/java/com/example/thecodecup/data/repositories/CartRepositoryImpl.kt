package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.remote.dto.CartItemCreateDto
import com.example.thecodecup.data.remote.dto.CartItemResponseDto
import com.example.thecodecup.data.remote.dto.FoodResponseDto
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.CartItemModel
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.models.FoodOptionModel
import com.example.thecodecup.domain.models.FoodOptionTypeModel
import com.example.thecodecup.domain.repositories.CartRepository
import com.example.thecodecup.utils.getHttpMessage
import retrofit2.HttpException

class CartRepositoryImpl(private val authPreferences: AuthPreferences) : CartRepository {
    private val api = ApiClient.cartApiService

    override suspend fun getCartItems() = call { api.getCartItems(token()).map(::toDomain) }

    override suspend fun addCartItem(foodId: Int, quantity: Int, optionTypeIds: List<Int>) =
        call {
            api.addCartItem(CartItemCreateDto(foodId, quantity, optionTypeIds), token())
        }

    override suspend fun updateCartItem(cartItemId: Int, foodId: Int, quantity: Int, optionTypeIds: List<Int>) =
        call { toDomain(api.updateCartItem(cartItemId = cartItemId, token = token(), cartItemInfo = CartItemCreateDto(foodId, quantity, optionTypeIds))) }

    override suspend fun updateQuantity(cartItemId: Int, quantity: Int) =
        call { toDomain(api.updateCartItemQuantity(token(), cartItemId, quantity)) }

    override suspend fun deleteCartItem(cartItemId: Int) =
        call { api.deleteCartItem(token(), cartItemId).map(::toDomain) }

    override suspend fun clearCart() =
        call { api.clearCart(token()).map(::toDomain) }

    private fun token() = "Bearer ${authPreferences.getAuthToken() ?: throw IllegalStateException("Please sign in again")}"

    private suspend fun <T> call(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: HttpException) {
        Result.failure(Exception(getHttpMessage(e)))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Unable to update cart"))
    }

    private fun toDomain(item: CartItemResponseDto) = CartItemModel(
        id = item.id,
        quantity = item.quantity,
        food = item.food.toDomain(),
        selectedOptionTypes = item.optionTypes.map { FoodOptionTypeModel(it.id, it.name, it.price) }
    )

    private fun FoodResponseDto.toDomain() = FoodModel(
        id, name, description, price, imageUrl, category, rewardPoint,
        options.map { option ->
            FoodOptionModel(option.id, option.name, option.foodOptionTypes.map {
                FoodOptionTypeModel(it.id, it.name, it.price)
            })
        }
    )
}
