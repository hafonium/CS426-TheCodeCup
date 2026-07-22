package com.example.thecodecup.domain.usecases.home

import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.repositories.FoodRepository

class GetFoodsUseCase(private val foodRepository: FoodRepository) {
    suspend operator fun invoke(): Result<List<FoodModel>> = foodRepository.getFoods()
}
