package com.example.thecodecup.data.remote.dto

import com.example.thecodecup.domain.models.UserResponseModel
import com.google.gson.annotations.SerializedName

data class UserCreateDto(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String
)

data class UserLoginDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UserResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("avatar_res_id") val avatarResId: String? = null
) {
    fun toDomainModel(): UserResponseModel {
        return UserResponseModel(
            id = id,
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber,
            avatarResId = avatarResId
        )
    }
}