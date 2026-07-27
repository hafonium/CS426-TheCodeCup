package com.example.thecodecup.domain.repositories

import com.example.thecodecup.domain.models.GainedRewardModel
import com.example.thecodecup.domain.models.GachaResultModel
import com.example.thecodecup.domain.models.PromotionModel
import com.example.thecodecup.domain.models.RedeemRewardModel

interface PromotionRepository {
    suspend fun getPromotion(): Result<PromotionModel>
    suspend fun getGainedRewards(): Result<List<GainedRewardModel>>
    suspend fun getRedeemRewards(): Result<List<RedeemRewardModel>>
    suspend fun redeemReward(rewardId: Int, address: String): Result<PromotionModel>
    suspend fun useGachapon(address: String): Result<GachaResultModel>
}
