package com.example.thecodecup.data.remote.network

import com.example.thecodecup.BuildConfig
import com.example.thecodecup.data.remote.api.AuthApiService
import com.example.thecodecup.data.remote.api.CartApiService
import com.example.thecodecup.data.remote.api.FoodApiService
import com.example.thecodecup.data.remote.api.OrderApiService
import com.example.thecodecup.data.remote.api.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
object ApiClient {
    private val API_URL = BuildConfig.API_URL

    // Create a logger so you can see exactly what is being sent/received in Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Build the OkHttp Client
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Good practice for network calls
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Build Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()) // Tells Retrofit how to parse JSON
        .build()

    // Expose the API Service
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val foodApiService: FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }

    val cartApiService: CartApiService by lazy {
        retrofit.create(CartApiService::class.java)
    }

    val orderApiService: OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }
}