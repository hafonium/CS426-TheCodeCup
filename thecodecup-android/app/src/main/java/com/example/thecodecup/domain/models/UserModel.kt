package com.example.thecodecup.domain.models


data class UserBaseModel (
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String? = null
)

data class UserResponseModel (
    val id: Int,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarResId: String? = null
)

data class UserCreateModel(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String
)

data class UserLoginModel(
    val email: String,
    val password: String
)