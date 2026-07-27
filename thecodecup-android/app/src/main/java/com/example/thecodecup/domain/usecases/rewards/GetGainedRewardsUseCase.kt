package com.example.thecodecup.domain.usecases.rewards

import com.example.thecodecup.domain.repositories.PromotionRepository

class GetGainedRewardsUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke() = repository.getGainedRewards()
}
