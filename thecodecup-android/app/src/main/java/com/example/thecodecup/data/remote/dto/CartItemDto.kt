package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartItemResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("food")
    val food: FoodResponseDto,

    @SerializedName("option_types")
    val optionTypes: List<FoodOptionTypeResponseDto>
)

data class CartItemEditResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("food")
    val food: FoodResponseDto,

    @SerializedName("options")
    val optionTypes: List<FoodOptionTypeEditResponseDto>
)

data class CartItemCreateDto(
    @SerializedName("food_id")
    val foodId: Int,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("option_type_ids")
    val optionTypeIds: List<Int>
)