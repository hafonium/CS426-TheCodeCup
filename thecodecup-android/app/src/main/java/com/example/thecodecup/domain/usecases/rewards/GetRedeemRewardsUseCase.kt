package com.example.thecodecup.domain.usecases.rewards

import com.example.thecodecup.domain.repositories.PromotionRepository

class GetRedeemRewardsUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke() = repository.getRedeemRewards()
}
