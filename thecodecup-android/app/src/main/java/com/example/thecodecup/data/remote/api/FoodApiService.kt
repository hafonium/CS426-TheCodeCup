package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.FoodResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApiService {
    @GET("foods")
    suspend fun getFoods(): List<FoodResponseDto>

    @GET("foods/{foodId}")
    suspend fun getFoodById(@Path("foodId") foodId: Int): FoodResponseDto
}