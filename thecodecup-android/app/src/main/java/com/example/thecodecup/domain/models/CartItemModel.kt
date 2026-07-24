package com.example.thecodecup.domain.models

data class CartItemModel(
    val id: Int,
    val quantity: Int,
    val food: FoodModel,
    val selectedOptionTypes: List<FoodOptionTypeModel>
) {
    val unitPrice: Double
        get() = food.price + selectedOptionTypes.sumOf { it.price }

    val totalPrice: Double
        get() = unitPrice * quantity
}
