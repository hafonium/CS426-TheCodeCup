package com.example.thecodecup.ui.auth.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun OtpVerificationScreen(
    email: String,
    title: String,
    viewModel: OtpVerificationViewModel,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var code by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is OtpUiState.Success) onVerified()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        BackButton(onClick = onBack, modifier = Modifier.size(44.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = CoffeeNavy,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = "Enter the 6-digit code sent to\n$email",
            color = CoffeeNavy.copy(alpha = .7f),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 28.dp)
        )
        CustomTextField(
            value = code,
            onValueChange = { code = it.filter(Char::isDigit).take(6) },
            placeholderText = "Verification code",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
        )
        val message = when (val current = state) {
            is OtpUiState.Error -> current.message
            is OtpUiState.CodeResent -> current.message
            else -> null
        }
        if (message != null) {
            Text(
                text = message,
                color = if (state is OtpUiState.Error) MaterialTheme.colorScheme.error else CoffeeBlue,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Button(
            onClick = { viewModel.verify(email, code) },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            backgroundGradient = listOf(CoffeeBlue, CoffeeBlue),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state is OtpUiState.Loading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                }
            } else {
                Text("Verify", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
        Text(
            text = "Didn't receive it? Resend code",
            color = CoffeeBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .clickable(enabled = state !is OtpUiState.Loading) { viewModel.resend(email) }
        )
    }
}
