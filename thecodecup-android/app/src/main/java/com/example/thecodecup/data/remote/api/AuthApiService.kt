package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.TokenDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginUser(
        @Field("username") email: String,
        @Field("password") password: String
    ): TokenDto

    @POST("auth/logout")
    suspend fun logoutUser(
        @Header("Authorization") token: String
    )
}