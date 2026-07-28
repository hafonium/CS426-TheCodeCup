package com.example.thecodecup.utils

import org.json.JSONObject
import retrofit2.HttpException

fun  getHttpMessage(e: HttpException): String {
    return getHttpError(e).message
}

data class HttpError(val code: String?, val message: String)

fun getHttpError(e: HttpException): HttpError {
    val errorBody = e.response()?.errorBody()?.string()
    return try {
        val root = JSONObject(errorBody ?: "")
        val nestedDetail = root.optJSONObject("detail")
        HttpError(
            code = root.optString("code").takeIf(String::isNotBlank)
                ?: nestedDetail?.optString("code")?.takeIf(String::isNotBlank),
            message = nestedDetail?.optString("detail")?.takeIf(String::isNotBlank)
                ?: nestedDetail?.optString("message")?.takeIf(String::isNotBlank)
                ?: root.optString("detail").takeIf(String::isNotBlank)
                ?: root.optString("message").takeIf(String::isNotBlank)
                ?: "An error occurred! Please try again."
        )
    } catch (_: Exception) {
        HttpError(null, "An error occurred! Please try again.")
    }
}
