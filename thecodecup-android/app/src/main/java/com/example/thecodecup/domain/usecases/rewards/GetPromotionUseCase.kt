package com.example.thecodecup.domain.usecases.rewards

import com.example.thecodecup.domain.repositories.PromotionRepository

class GetPromotionUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke() = repository.getPromotion()
}
