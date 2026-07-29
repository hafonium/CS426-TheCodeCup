package com.example.thecodecup.data.local.prefs

import android.content.Context

class ThemePreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean = preferences.getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(isDarkMode: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "CodeCupThemePreferences"
        const val KEY_DARK_MODE = "dark_mode"
    }
}
