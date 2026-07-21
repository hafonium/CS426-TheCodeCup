package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.models.UserCreateModel
import com.example.thecodecup.domain.models.UserResponseModel
import com.example.thecodecup.domain.models.UserUpdateModel

interface UserRepository {
    suspend fun registerUser(user: UserCreateModel): Result<Unit>

    suspend fun getCurrentUser(): Result<UserResponseModel>
    suspend fun updateUser(user: UserUpdateModel): Result<Unit>
}