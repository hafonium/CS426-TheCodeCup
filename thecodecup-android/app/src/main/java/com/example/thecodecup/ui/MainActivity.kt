package com.example.thecodecup.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.thecodecup.data.local.prefs.ThemePreferences
import com.example.thecodecup.ui.theme.TheCodeCupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePreferences = remember { ThemePreferences(applicationContext) }
            var isDarkMode by remember { mutableStateOf(themePreferences.isDarkMode()) }

            TheCodeCupTheme(
                darkTheme = isDarkMode,
                onDarkThemeChange = { enabled ->
                    isDarkMode = enabled
                    themePreferences.setDarkMode(enabled)
                }
            ) {
                TheCodeCupApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    TheCodeCupTheme {
        TheCodeCupApp()
    }
}
