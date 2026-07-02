package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.UserCreateDto
import com.example.thecodecup.data.remote.dto.UserResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST

interface UserApiService {
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): UserResponseDto

    @GET("users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserResponseDto

    @POST("users")
    suspend fun createUser(@Body user: UserCreateDto)
}