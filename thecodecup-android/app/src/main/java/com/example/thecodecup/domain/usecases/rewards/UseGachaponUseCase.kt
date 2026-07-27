package com.example.thecodecup.domain.usecases.rewards

import com.example.thecodecup.domain.repositories.PromotionRepository

class UseGachaponUseCase(private val repository: PromotionRepository) {
    suspend operator fun invoke(address: String) = repository.useGachapon(address)
}
