package com.example.thecodecup.domain.usecases.auth

import com.example.thecodecup.domain.repositories.UserRepository

class ChangeForgotPasswordUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        if (password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }
        return userRepository.changeForgotPassword(email.trim(), password)
    }
}
