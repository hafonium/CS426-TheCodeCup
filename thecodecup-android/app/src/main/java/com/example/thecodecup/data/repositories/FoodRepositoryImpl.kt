package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.repositories.FoodRepository
import com.example.thecodecup.utils.getHttpMessage
import retrofit2.HttpException

class FoodRepositoryImpl : FoodRepository {
    private val api = ApiClient.foodApiService

    override suspend fun getFoods(): Result<List<FoodModel>> = try {
        Result.success(api.getFoods().map { food ->
            FoodModel(
                id = food.id,
                name = food.name,
                description = food.description,
                price = food.price,
                // Gson can populate a Kotlin non-null DTO property with null when
                // the backend omits the field or explicitly returns JSON null.
                imageUrl = (food.imageUrl as String?).orEmpty(),
                category = food.category,
                rewardPoint = food.rewardPoint
            )
        })
    } catch (e: HttpException) {
        Result.failure(Exception(getHttpMessage(e)))
    } catch (e: Exception) {
        Result.failure(Exception("Failed to fetch coffee: ${e.message}"))
    }
}
