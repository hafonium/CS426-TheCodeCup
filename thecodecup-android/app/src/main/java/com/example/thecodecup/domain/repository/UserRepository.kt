package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.models.UserModel

interface UserRepository {
    suspend fun getUserById(userId: Int): UserModel?
    suspend fun registerUser(user: UserModel, password: String)
    suspend fun loginUser(email: String, password: String): UserModel?
    suspend fun updateUserExceptPassword(user: UserModel)
    suspend fun deleteUser(userId: Int)
    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String)
    suspend fun checkIfEmailExists(email: String): Boolean
    suspend fun checkIfPhoneExists(phone: String): Boolean
}