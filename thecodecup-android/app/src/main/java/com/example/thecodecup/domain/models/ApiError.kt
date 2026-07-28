package com.example.thecodecup.domain.models

class ApiException(
    val code: String?,
    message: String
) : Exception(message)
