package com.example.thecodecup.domain.usecase.account

import androidx.compose.ui.platform.LocalContext
import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.domain.repository.UserRepository

class LoginUseCase (
    private val userRepository: UserRepository,
    private val authPrefs: AuthPreferences
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        // Check for empty fields
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password are required."))
        }

        return try {
            val user = userRepository.loginUser(email, password)
            if (user != null) {
                authPrefs.saveUserSession(user.id)
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid email or password."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}