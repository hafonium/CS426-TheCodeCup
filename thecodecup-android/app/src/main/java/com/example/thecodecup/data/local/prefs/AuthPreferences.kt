package com.example.thecodecup.data.local.prefs

import android.content.Context

class AuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("CodeCupPrefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun clearAuthToken() {
        prefs.edit().remove("access_token").apply()
    }
}