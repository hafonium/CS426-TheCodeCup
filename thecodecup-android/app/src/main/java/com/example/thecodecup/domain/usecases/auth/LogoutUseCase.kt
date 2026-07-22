package com.example.thecodecup.domain.usecases.auth


import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.domain.repositories.AuthRepository

class LogoutUseCase (
        private val authRepository: AuthRepository,
        private val authPrefs: AuthPreferences
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            val result = authRepository.logoutUser()
            result.fold(
                onSuccess = {
                    authPrefs.clearAuthToken()
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
