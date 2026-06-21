package com.example.thecodecup.data.local.prefs

import android.content.Context

class AuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("CodeCupPrefs", Context.MODE_PRIVATE)

    fun saveUserSession(userId: Int) {
        prefs.edit().putBoolean("isLoggedIn", true).putInt("currentUserId", userId).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }
}