package com.example.thecodecup.utils

import org.json.JSONObject
import retrofit2.HttpException

fun  getHttpMessage(e: HttpException): String {
    val errorBody = e.response()?.errorBody()?.string()
    val errorMessage = try {
        val jsonObject = JSONObject(errorBody ?: "")
        jsonObject.getString("detail")
    } catch (_: Exception) {
        "An error occurred! Please try again."
    }
    return errorMessage
}