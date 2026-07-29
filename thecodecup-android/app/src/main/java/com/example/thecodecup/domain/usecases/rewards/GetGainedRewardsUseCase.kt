package com.example.thecodecup.domain.usecases.rewards

import com.example.thecodecup.domain.repositories.PromotionRepository

class GetGainedRewardsUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke(limit: Int = 8, offset: Int = 0) =
        repository.getGainedRewards(limit = limit, offset = offset)
}
