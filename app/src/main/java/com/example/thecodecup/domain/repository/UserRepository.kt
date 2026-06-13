package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.models.UserModel

interface UserRepository {
    suspend fun getUserById(userId: Int): UserModel?
    suspend fun insertUser(user: UserModel, password: String)
}