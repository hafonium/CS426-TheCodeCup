package com.example.thecodecup.ui.auth.login

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onNavigateToWelcome : () -> Unit = { },
    onNavigateToRegister : () -> Unit = { },
    onNavigateToHome : () -> Unit = { }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            viewModel.resetState()
            onNavigateToHome()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onLoginClicked = { email, password ->
            viewModel.onLoginClicked(email, password)
        },
        onNavigateToWelcome = onNavigateToWelcome,
        onNavigateToRegister = onNavigateToRegister,
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "Idle State")
@Composable
fun LoginScreenPreview_Idle() {
     ScreenWrapper {
         LoginScreenContent(
             uiState = LoginUiState.Idle,
             onLoginClicked = { _, _ -> },
             onNavigateToWelcome = {},
             onNavigateToRegister = {}
         )
     }
 }

@Preview(showBackground = true, name = "Loading State")
@Composable
fun LoginScreenPreview_Loading() {
    ScreenWrapper {
        LoginScreenContent(
            uiState = LoginUiState.Loading,
            onLoginClicked = { _, _ -> },
            onNavigateToWelcome = {},
            onNavigateToRegister = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun LoginScreenPreview_Error() {
    ScreenWrapper {
        LoginScreenContent(
            uiState = LoginUiState.Error("Invalid email or password"),
            onLoginClicked = { _, _ -> },
            onNavigateToWelcome = {},
            onNavigateToRegister = {}
        )
    }
}