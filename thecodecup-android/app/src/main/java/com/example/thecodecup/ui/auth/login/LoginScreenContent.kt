package com.example.thecodecup.ui.auth.login

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.auth.register.RegisterUiState
import com.example.thecodecup.ui.components.CustomTextField
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.components.buttons.Button
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onLoginClicked: (String, String) -> Unit,
    onNavigateToWelcome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(14.dp)
    val focusManager = LocalFocusManager.current
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val isFormComplete = emailText.isNotBlank() && passwordText.isNotBlank()
    val imeAction = if (isFormComplete) ImeAction.Done else ImeAction.Next
    val keyboardActions = if (isFormComplete) {
        KeyboardActions(
            onDone = {
                focusManager.clearFocus() // Closes the keyboard
                // Trigger the registration!
                if (uiState !is LoginUiState.Loading) {
                    onLoginClicked(emailText, passwordText)
                }
            }
        )
    } else {
        KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        BackButton(
            onClick = onNavigateToWelcome,
            modifier = Modifier.size(44.dp)
        )

        Text(
            text = "Welcome back! Glad to see you, Again!",
            style = MaterialTheme.typography.headlineMedium,
            color = CoffeeNavy,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(bottom = 24.dp)

        )


        CustomTextField(
            value = emailText,
            onValueChange = { emailText = it },
            placeholderText = "Enter your email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        CustomTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            placeholderText = "Enter your password",
            canToggleVisibility = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Forgot your password?",
            style = MaterialTheme.typography.bodyMedium,
            color = CoffeeBlue,
            modifier = Modifier
                .clickable {
                    onNavigateToForgotPassword(emailText.trim())
                }
                .align(Alignment.End)
        )

        if (uiState is LoginUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Button(
            onClick = {
                if (uiState !is LoginUiState.Loading) {
                    onLoginClicked(emailText, passwordText)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(bottom = 16.dp)
                .padding(bottom = 16.dp),
            backgroundGradient = listOf(CoffeeBlue, CoffeeBlue),
            shape = buttonShape,
        ) {
            if (uiState is LoginUiState.Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "Login",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = "Don't have an account? Sign up",
            style = MaterialTheme.typography.bodyMedium,
            color = CoffeeBlue,
            modifier = Modifier
                .clickable {
                    onNavigateToRegister()
                }
                .align(Alignment.CenterHorizontally)
        )
    }
}
