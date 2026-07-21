package com.example.thecodecup.data.repository

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.remote.dto.UserCreateDto
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.UserCreateModel
import com.example.thecodecup.domain.models.UserResponseModel
import com.example.thecodecup.domain.models.UserUpdateModel
import com.example.thecodecup.domain.repository.UserRepository
import com.example.thecodecup.ui.auth.register.RegisterScreen
import com.example.thecodecup.utils.getHttpMessage
import org.json.JSONObject
import retrofit2.HttpException

class UserRepositoryImpl(
    private val authPreferences: AuthPreferences
): UserRepository {
    private val api = ApiClient.userApiService

    override suspend fun registerUser(user: UserCreateModel): Result<Unit> {
        val userCreate = user.toDto()
        return try {
            api.createUser(userCreate)
            Result.success(Unit)
        } catch (e: HttpException) {
            val errorMessage = getHttpMessage(e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to register user: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): Result<UserResponseModel> {
        return try {
            val token = authPreferences.getAuthToken()
                ?: return Result.failure(Exception("No auth token found"))

            val authToken = "Bearer $token"
            val userResponse = api.getCurrentUser(authToken)
            Result.success(userResponse.toDomainModel())
        } catch (e: HttpException) {
            val errorMessage = getHttpMessage(e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch current user: ${e.message}"))
        }
    }

    override suspend fun updateUser(user: UserUpdateModel): Result<Unit> {
        return try {
            val token = authPreferences.getAuthToken()
                ?: return Result.failure(Exception("No auth token found"))

            val authToken = "Bearer $token"
            api.updateUser(authToken, user.toDto())
            Result.success(Unit)
        } catch (e: HttpException) {
            val errorMessage = getHttpMessage(e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update user: ${e.message}"))
        }
    }
}