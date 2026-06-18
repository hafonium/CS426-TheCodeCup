package com.example.thecodecup.ui

import androidx.compose.runtime.Composable
import com.example.thecodecup.ui.navigation.ScreenNavigator
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun TheCodeCupApp() {
    ScreenWrapper {
        ScreenNavigator()
    }
}