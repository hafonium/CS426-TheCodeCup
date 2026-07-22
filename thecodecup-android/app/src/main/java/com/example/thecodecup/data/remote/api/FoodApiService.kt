package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.FoodResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {
    @GET("foods")
    suspend fun getFoods(
        @Query("category") category: String? = null
    ): List<FoodResponseDto>

    @GET("foods/{foodId}")
    suspend fun getFoodById(@Path("foodId") foodId: Int): FoodResponseDto
}