package com.example.thecodecup.domain.models

data class FoodModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val rewardPoint: Int,
    val options: List<FoodOptionModel> = emptyList()
)

data class FoodOptionModel(
    val id: Int,
    val name: String,
    val types: List<FoodOptionTypeModel>
)

data class FoodOptionTypeModel(
    val id: Int?,
    val name: String,
    val price: Double
)
