package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.RedeemRewardResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RedeemRewardApiService {
    @GET("redeem-rewards")
    suspend fun getRedeemRewards(
        @Query("limit") limit: Int = 8,
        @Query("offset") offset: Int = 0
    ): List<RedeemRewardResponseDto>

    @GET("redeem-rewards/{rewardId}")
    suspend fun getRedeemRewardById(
        @retrofit2.http.Path("rewardId") rewardId: Int
    ): RedeemRewardResponseDto
}