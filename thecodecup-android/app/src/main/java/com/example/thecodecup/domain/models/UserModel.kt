package com.example.thecodecup.domain.models

import com.example.thecodecup.data.remote.dto.UserCreateDto
import com.example.thecodecup.data.remote.dto.UserUpdateDto


data class UserBaseModel (
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String? = null,
    val address: String
)

data class UserResponseModel (
    val id: Int,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String? = null,
    val address: String
)

data class UserCreateModel(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val address: String
) {
    fun toDto(): UserCreateDto {
        return UserCreateDto(
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phoneNumber,
            address = address
        )
    }
}

data class UserLoginModel(
    val email: String,
    val password: String
)

data class UserUpdateModel(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String? = null,
    val address: String,
    val oldPassword: String? = null,
    val newPassword: String? = null
) {
    fun  toDto(): UserUpdateDto {
        return UserUpdateDto(
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber,
            avatarResId = avatarUrl,
            address = address,
            oldPassword = oldPassword,
            newPassword = newPassword
        )
    }
}