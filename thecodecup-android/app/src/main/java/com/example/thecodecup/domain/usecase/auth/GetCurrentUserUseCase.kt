package com.example.thecodecup.domain.usecase.auth

import com.example.thecodecup.domain.models.UserResponseModel
import com.example.thecodecup.domain.repository.UserRepository

class GetCurrentUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Result<UserResponseModel> {
        return try {
            val userResult = userRepository.getCurrentUser()
            userResult.fold(
                onSuccess = { user ->
                    Result.success(user)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}