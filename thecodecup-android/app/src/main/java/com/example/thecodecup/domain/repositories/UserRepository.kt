package com.example.thecodecup.domain.repositories

import com.example.thecodecup.domain.models.UserCreateModel
import com.example.thecodecup.domain.models.UserResponseModel
import com.example.thecodecup.domain.models.UserUpdateModel

interface UserRepository {
    suspend fun registerUser(user: UserCreateModel): Result<Unit>
    suspend fun sendOtp(email: String): Result<Unit>
    suspend fun verifyEmail(email: String, otpCode: String): Result<Unit>
    suspend fun verifyForgotPassword(email: String, otpCode: String): Result<Unit>
    suspend fun changeForgotPassword(email: String, newPassword: String): Result<Unit>

    suspend fun getCurrentUser(): Result<UserResponseModel>
    suspend fun updateUser(user: UserUpdateModel): Result<Unit>
}
