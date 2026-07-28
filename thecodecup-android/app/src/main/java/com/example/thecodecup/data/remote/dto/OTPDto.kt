package com.example.thecodecup.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OTPResponseDto(
    @SerializedName("attempt_remaining")
    val attemptRemaining: Int,

    @SerializedName("is_successful")
    val isSuccessful: Boolean
)

data class OTPCreateDto(
    @SerializedName("email")
    val email: String
)

data class OTPVerificationDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("otp_code")
    val otpCode: String
)