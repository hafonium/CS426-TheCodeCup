package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.models.FoodOptionModel
import com.example.thecodecup.domain.models.FoodOptionTypeModel
import com.example.thecodecup.domain.repositories.FoodRepository
import com.example.thecodecup.utils.getHttpMessage
import retrofit2.HttpException

class FoodRepositoryImpl : FoodRepository {
    private val api = ApiClient.foodApiService

    override suspend fun getFoods(): Result<List<FoodModel>> = try {
        Result.success(api.getFoods().map(::toDomain))
    } catch (e: HttpException) {
        Result.failure(Exception(getHttpMessage(e)))
    } catch (e: Exception) {
        Result.failure(Exception("Failed to fetch coffee: ${e.message}"))
    }

    override suspend fun getFoodById(foodId: Int): Result<FoodModel> = try {
        Result.success(toDomain(api.getFoodById(foodId)))
    } catch (e: HttpException) {
        Result.failure(Exception(getHttpMessage(e)))
    } catch (e: Exception) {
        Result.failure(Exception("Failed to fetch coffee details: ${e.message}"))
    }

    private fun toDomain(food: com.example.thecodecup.data.remote.dto.FoodResponseDto) = FoodModel(
        id = food.id,
        name = food.name,
        description = food.description,
        price = food.price,
        imageUrl = (food.imageUrl as String?).orEmpty(),
        category = food.category,
        rewardPoint = food.rewardPoint,
        options = (food.options as List<com.example.thecodecup.data.remote.dto.FoodOptionResponseDto>?)
            .orEmpty()
            .map { option ->
                FoodOptionModel(
                    id = option.id,
                    name = option.name,
                    types = option.foodOptionTypes.map { type ->
                        FoodOptionTypeModel(type.id, type.name, type.price)
                    }
                )
            }
    )
}
