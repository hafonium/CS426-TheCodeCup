package com.example.thecodecup.data.remote.dto

import com.example.thecodecup.domain.models.UserResponseModel
import com.google.gson.annotations.SerializedName
import java.io.Serial

data class UserCreateDto(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("address") val address: String
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
    @SerializedName("avatar_res_id") val avatarResId: String? = null,
    @SerializedName("address") val address: String
) {
    fun toDomainModel(): UserResponseModel {
        return UserResponseModel(
            id = id,
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber,
            avatarUrl = avatarResId,
            address = address
        )
    }
}

data class UserUpdateDto(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("avatar_res_id") val avatarResId: String? = null,
    @SerializedName("address") val address: String,
    @SerializedName("old_password") val oldPassword: String? = null,
    @SerializedName("new_password") val newPassword: String? = null
)

data class UserChangeForgotPasswordDto(
    @SerializedName("email") val email: String,
    @SerializedName("new_password") val newPassword: String
)