package com.example.thecodecup.data.remote.api

import com.example.thecodecup.data.remote.dto.OTPCreateDto
import com.example.thecodecup.data.remote.dto.OTPResponseDto
import com.example.thecodecup.data.remote.dto.OTPVerificationDto
import retrofit2.http.Body
import retrofit2.http.POST

interface OTPApiService {
    @POST("otp/send")
    suspend fun sendOTP(
        @Body otp: OTPCreateDto
    ): OTPResponseDto

    @POST("otp/verify-email")
    suspend fun verifyEmail(
        @Body otp: OTPVerificationDto
    ): OTPResponseDto

    @POST("otp/verify-forgot-password")
    suspend fun verifyForgotPassword(
        @Body otp: OTPVerificationDto
    ): OTPResponseDto
}