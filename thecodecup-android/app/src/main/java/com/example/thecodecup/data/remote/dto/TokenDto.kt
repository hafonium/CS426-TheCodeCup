package com.example.thecodecup.data.remote.dto

import com.example.thecodecup.domain.models.TokenModel
import com.google.gson.annotations.SerializedName

data class TokenDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
) {
    fun toDomainModel(): TokenModel {
        return TokenModel(
            accessToken = accessToken,
            tokenType = tokenType
        )
    }
}

data class TokenDataDto(
    @SerializedName("email") val email: String
)