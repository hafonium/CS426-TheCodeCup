package com.example.thecodecup.ui.auth.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.components.CustomTextField
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.components.buttons.Button
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun ForgotPasswordEmailScreen(
    initialEmail: String,
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit,
    onCodeSent: (String) -> Unit
) {
    var email by remember(initialEmail) { mutableStateOf(initialEmail) }
    LaunchedEffect(initialEmail) {
        if (initialEmail.isNotBlank()) {
            viewModel.sendCode(initialEmail)
        }
    }
    RecoveryForm(
        title = "Forgot password?",
        description = "Enter your email and we'll send you a verification code.",
        state = viewModel.uiState.collectAsState().value,
        onBack = onBack,
        fields = {
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Enter your email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
        },
        buttonText = "Send code",
        onSubmit = { viewModel.sendCode(email) },
        onSuccess = { onCodeSent(email.trim()) }
    )
}

@Composable
fun ChangeForgotPasswordScreen(
    email: String,
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    RecoveryForm(
        title = "Create new password",
        description = "Choose a strong password for $email.",
        state = viewModel.uiState.collectAsState().value,
        onBack = onBack,
        fields = {
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "New password",
                canToggleVisibility = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            CustomTextField(
                value = confirmation,
                onValueChange = { confirmation = it },
                placeholderText = "Confirm new password",
                canToggleVisibility = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
        },
        buttonText = "Change password",
        onSubmit = { viewModel.changePassword(email, password, confirmation) },
        onSuccess = onPasswordChanged
    )
}

@Composable
private fun RecoveryForm(
    title: String,
    description: String,
    state: ForgotPasswordUiState,
    onBack: () -> Unit,
    fields: @Composable () -> Unit,
    buttonText: String,
    onSubmit: () -> Unit,
    onSuccess: () -> Unit
) {
    LaunchedEffect(state) {
        if (state is ForgotPasswordUiState.Success) onSuccess()
    }
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        BackButton(onClick = onBack, modifier = Modifier.size(44.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = CoffeeNavy,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            color = CoffeeNavy.copy(alpha = .7f),
            modifier = Modifier.padding(top = 12.dp, bottom = 28.dp)
        )
        fields()
        if (state is ForgotPasswordUiState.Error) {
            Text(
                state.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            backgroundGradient = listOf(CoffeeBlue, CoffeeBlue),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state is ForgotPasswordUiState.Loading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                }
            } else {
                Text(buttonText, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }
}
