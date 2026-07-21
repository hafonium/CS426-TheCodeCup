package com.example.thecodecup.ui.auth.register

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToWelcome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            viewModel.resetState()
            onNavigateToLogin()
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegisterClicked = { name, email, phone, address, pass, confirm ->
            viewModel.onRegisterClicked(name, email, phone, address, pass, confirm)
        },
        onNavigateToWelcome = onNavigateToWelcome,
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "Idle State")
@Composable
fun RegisterScreenPreview_Idle() {
    ScreenWrapper {
        RegisterScreenContent(
            uiState = RegisterUiState.Idle,
            onRegisterClicked = { _, _, _, _, _, _ -> },
            onNavigateToWelcome = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun RegisterScreenPreview_Loading() {
    ScreenWrapper {
        RegisterScreenContent(
            uiState = RegisterUiState.Loading,
            onRegisterClicked = { _, _, _, _, _, _ -> },
            onNavigateToWelcome = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun RegisterScreenPreview_Error() {
    ScreenWrapper {
        RegisterScreenContent(
            uiState = RegisterUiState.Error("Passwords do not match"),
            onRegisterClicked = { _, _, _, _, _, _ -> },
            onNavigateToWelcome = {},
            onNavigateToLogin = {}
        )
    }
}