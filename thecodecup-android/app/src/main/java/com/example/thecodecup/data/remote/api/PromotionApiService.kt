package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.GachaResponseDto
import com.example.thecodecup.data.remote.dto.GachaUseDto
import com.example.thecodecup.data.remote.dto.GainedRewardResponseDto
import com.example.thecodecup.data.remote.dto.PromotionResponseDto
import com.example.thecodecup.data.remote.dto.RedeemRewardUseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PromotionApiService {
    @GET("promotions")
    suspend fun getPromotions(
        @Header("Authorization") token: String
    ): PromotionResponseDto

    @GET("promotions/gained-rewards")
    suspend fun getGainedRewards(
        @Header("Authorization") token: String
    ): List<GainedRewardResponseDto>

    @POST("promotions/reward-point")
    suspend fun useRewardPoint(
        @Header("Authorization") token: String,
        @Body redeemRewardUseDto: RedeemRewardUseDto
    ): PromotionResponseDto

    @POST("promotions/gachapon")
    suspend fun useGachapon(
        @Header("Authorization") token: String,
        @Body gachaUse: GachaUseDto
    ): GachaResponseDto
}