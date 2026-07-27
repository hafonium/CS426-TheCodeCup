package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.RedeemRewardResponseDto
import retrofit2.http.GET

interface RedeemRewardApiService {
    @GET("redeem-rewards")
    suspend fun getRedeemRewards(): List<RedeemRewardResponseDto>

    @GET("redeem-rewards/{rewardId}")
    suspend fun getRedeemRewardById(
        @retrofit2.http.Path("rewardId") rewardId: Int
    ): RedeemRewardResponseDto
}