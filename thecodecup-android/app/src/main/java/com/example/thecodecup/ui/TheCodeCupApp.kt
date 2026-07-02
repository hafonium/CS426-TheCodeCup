package com.example.thecodecup.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thecodecup.ui.auth.auth.AuthViewModel
import com.example.thecodecup.ui.navigation.ScreenNavigator
import com.example.thecodecup.utils.ScreenWrapper



@Composable
fun TheCodeCupApp() {
    val context = LocalContext.current
    val app = context.applicationContext as App
    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AuthViewModel(app.getCurrentUserUseCase)
            }
        }
    )

    ScreenWrapper {
        ScreenNavigator(authViewModel = authViewModel)
    }
}