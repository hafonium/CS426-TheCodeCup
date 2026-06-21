package com.example.thecodecup.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.thecodecup.ui.theme.TheCodeCupTheme

@Composable
fun ScreenWrapper(content: @Composable () -> Unit) {
    TheCodeCupTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            content()
        }
    }
}