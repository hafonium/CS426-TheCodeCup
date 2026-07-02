package com.example.thecodecup.domain.usecase.auth

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.domain.models.UserLoginModel
import com.example.thecodecup.domain.repository.AuthRepository

class LoginUseCase (
    private val authRepository: AuthRepository,
    private val authPrefs: AuthPreferences
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        // Check for empty fields
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password are required."))
        }

        val userLoginModel = UserLoginModel(email = email, password = password)
        return try {
            val authToken = authRepository.loginUser(userLoginModel)
            authToken.fold(
                onSuccess = { authToken ->
                    // Save the token in SharedPreferences
                    authPrefs.saveAuthToken(authToken.accessToken)
                    Result.success(Unit)
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