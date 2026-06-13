package com.example.thecodecup.domain.models

import com.example.thecodecup.data.local.entities.UserEntity

data class UserModel(
    val id: Int,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String
) {
    fun toEntity(userPassword: String): UserEntity {
        return UserEntity(
            id = id,
            email = email,
            password = userPassword,
            fullName = fullName,
            phoneNumber = phoneNumber,
            avatarUrl = avatarUrl
        )
    }
}