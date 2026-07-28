package com.example.thecodecup.domain.usecases.auth

import com.example.thecodecup.domain.repositories.UserRepository

class SendOtpUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank() || !EMAIL_REGEX.matches(email.trim())) {
            return Result.failure(Exception("Enter a valid email address"))
        }
        return userRepository.sendOtp(email.trim())
    }
}

internal val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
