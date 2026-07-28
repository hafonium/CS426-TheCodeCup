package com.example.thecodecup.domain.usecases.auth

import com.example.thecodecup.domain.repositories.UserRepository

class VerifyForgotPasswordUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, otpCode: String): Result<Unit> {
        if (otpCode.length != 6 || !otpCode.all(Char::isDigit)) {
            return Result.failure(Exception("Enter the 6-digit verification code"))
        }
        return userRepository.verifyForgotPassword(email.trim(), otpCode)
    }
}
