package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.TokenModel
import com.example.thecodecup.domain.models.UserLoginModel
import com.example.thecodecup.domain.models.ApiException
import com.example.thecodecup.domain.repositories.AuthRepository
import com.example.thecodecup.utils.getHttpMessage
import com.example.thecodecup.utils.getHttpError
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val authPreferences: AuthPreferences
): AuthRepository {
    private val api = ApiClient.authApiService
//    the JWT token back to the UseCase
    override suspend fun loginUser(user: UserLoginModel): Result<TokenModel> {
        return try {
            val tokenModel = api.loginUser(user.email, user.password)
            Result.success(tokenModel.toDomainModel())
        } catch(e: HttpException) {
            val error = getHttpError(e)
            Result.failure(ApiException(error.code, error.message))
        } catch(e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun logoutUser(): Result<Unit> {
        val token = authPreferences.getAuthToken()
            ?: return Result.failure(Exception("No auth token found"))

        return try {
            val authToken = "Bearer $token"
            api.logoutUser(authToken)
            Result.success(Unit)
        } catch(e: HttpException) {
            val errorMessage = getHttpMessage(e)
            Result.failure(Exception(errorMessage))
        } catch(e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}
