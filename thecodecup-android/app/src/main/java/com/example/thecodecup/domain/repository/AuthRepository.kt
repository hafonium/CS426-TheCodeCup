package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.models.TokenModel
import com.example.thecodecup.domain.models.UserLoginModel

interface AuthRepository {
    suspend fun loginUser(user: UserLoginModel): Result<TokenModel>
    suspend fun logoutUser(): Result<Unit>
}