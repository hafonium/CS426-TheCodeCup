package com.example.thecodecup.ui.core.profile

import androidx.compose.lint.Names.Runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.ui.auth.register.RegisterUiState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    onNavigateToWelcome: () -> Unit = {},
    onNavigateToHome : () -> Unit = { }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.LoggedOut) {
            viewModel.resetState()
            onNavigateToWelcome()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
    }

    ProfileScreenContent(
        uiState = uiState,
        onLogoutClicked = {viewModel.onLogoutClicked()},
        fetchCurrentUser = {viewModel.fetchCurrentUser()},
        onUpdateClicked = { email, fullName, phone, avatarUrl, address, oldPassword, newPassword, confirmNewPassword ->
            viewModel.updateUser(email, fullName, phone, avatarUrl, address, oldPassword, newPassword, confirmNewPassword)
        },
        onNavigateToHome = onNavigateToHome,
        modifier = modifier
    )
}
