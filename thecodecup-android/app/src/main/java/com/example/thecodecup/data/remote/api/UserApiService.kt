package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.UserChangeForgotPasswordDto
import com.example.thecodecup.data.remote.dto.UserCreateDto
import com.example.thecodecup.data.remote.dto.UserResponseDto
import com.example.thecodecup.data.remote.dto.UserUpdateDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApiService {
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): UserResponseDto

    @GET("users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserResponseDto

    @POST("users")
    suspend fun createUser(@Body user: UserCreateDto)

    @PUT("users/me")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: UserUpdateDto
    )

    @PATCH("users/change-forgot-password")
    suspend fun changeForgotPassword(
        @Body user: UserChangeForgotPasswordDto
    )
}