package com.example.thecodecup.domain.usecases.details

import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.repositories.FoodRepository

class GetFoodDetailsUseCase(private val foodRepository: FoodRepository) {
    suspend operator fun invoke(foodId: Int): Result<FoodModel> =
        foodRepository.getFoodById(foodId)
}
