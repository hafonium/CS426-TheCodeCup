package com.example.thecodecup.domain.usecases.rewards

import com.example.thecodecup.domain.repositories.PromotionRepository

class RedeemRewardUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke(rewardId: Int, address: String) =
        repository.redeemReward(rewardId, address)
}
