package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.models.UserCreateModel
import com.example.thecodecup.domain.models.UserResponseModel

interface UserRepository {
    suspend fun registerUser(user: UserCreateModel): Result<Unit>

    suspend fun getCurrentUser(): Result<UserResponseModel>
}