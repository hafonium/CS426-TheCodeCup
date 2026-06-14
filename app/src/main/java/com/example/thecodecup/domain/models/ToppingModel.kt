package com.example.thecodecup.domain.models

data class ToppingModel(
    val id: Int,
    val name: String,
    val price: Float,
    val category: String,
    val imageResId: Int,
    val isSelected: Boolean = false
) {
    val formattedPrice: String
        get() = "+$${String.format("%.2f", price)}"
}
