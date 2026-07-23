package com.example.thecodecup.domain.repositories

import com.example.thecodecup.domain.models.FoodModel

interface FoodRepository {
    suspend fun getFoods(): Result<List<FoodModel>>
    suspend fun getFoodById(foodId: Int): Result<FoodModel>
}
