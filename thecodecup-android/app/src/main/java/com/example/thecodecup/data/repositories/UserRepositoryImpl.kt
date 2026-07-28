package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.data.remote.dto.OTPCreateDto
import com.example.thecodecup.data.remote.dto.OTPVerificationDto
import com.example.thecodecup.data.remote.dto.UserChangeForgotPasswordDto
import com.example.thecodecup.domain.models.UserCreateModel
import com.example.thecodecup.domain.models.UserResponseModel
import com.example.thecodecup.domain.models.UserUpdateModel
import com.example.thecodecup.domain.models.ApiException
import com.example.thecodecup.domain.repositories.UserRepository
import com.example.thecodecup.utils.getHttpMessage
import com.example.thecodecup.utils.getHttpError
import retrofit2.HttpException

class UserRepositoryImpl(
    private val authPreferences: AuthPreferences
): UserRepository {
    private val api = ApiClient.userApiService
    private val otpApi = ApiClient.otpApiService

    override suspend fun registerUser(user: UserCreateModel): Result<Unit> {
        val userCreate = user.toDto()
        return try {
            api.createUser(userCreate)
            Result.success(Unit)
        } catch (e: HttpException) {
            val error = getHttpError(e)
            Result.failure(ApiException(error.code, error.message))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to register user: ${e.message}"))
        }
    }

    override suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            // The send endpoint returns an informational message rather than OTPResponseDto.
            // A completed Retrofit call means the OTP was generated and queued for delivery.
            otpApi.sendOTP(OTPCreateDto(email))
            Result.success(Unit)
        } catch (e: HttpException) {
            val error = getHttpError(e)
            Result.failure(ApiException(error.code, error.message))
        } catch (e: Exception) {
            Result.failure(Exception("OTP request failed: ${e.message}"))
        }
    }

    override suspend fun verifyEmail(email: String, otpCode: String): Result<Unit> =
        executeOtpRequest {
            otpApi.verifyEmail(OTPVerificationDto(email, otpCode))
        }

    override suspend fun verifyForgotPassword(email: String, otpCode: String): Result<Unit> =
        executeOtpRequest {
            otpApi.verifyForgotPassword(OTPVerificationDto(email, otpCode))
        }

    override suspend fun changeForgotPassword(
        email: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            api.changeForgotPassword(UserChangeForgotPasswordDto(email, newPassword))
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception(getHttpMessage(e)))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to change password: ${e.message}"))
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

    private suspend fun executeOtpRequest(
        request: suspend () -> com.example.thecodecup.data.remote.dto.OTPResponseDto
    ): Result<Unit> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("OTP request failed. ${response.attemptRemaining} attempts remaining")
                )
            }
        } catch (e: HttpException) {
            Result.failure(Exception(getHttpMessage(e)))
        } catch (e: Exception) {
            Result.failure(Exception("OTP request failed: ${e.message}"))
        }
    }
}
