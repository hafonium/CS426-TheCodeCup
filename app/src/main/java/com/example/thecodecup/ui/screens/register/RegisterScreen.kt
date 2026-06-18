package com.example.thecodecup.ui.screens.register

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.components.CustomTextField
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.components.buttons.Button
import com.example.thecodecup.ui.theme.White

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToWelcome : () -> Unit = { },
    onNavigateToLogin : () -> Unit = { }
) {
    val buttonShape = RoundedCornerShape(percent = 15)
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
    ) {
        BackButton(
            onClick = onNavigateToWelcome,
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = buttonShape
                )
                .size(48.dp)
        )

        Text(
            text = "Hello! Register to get started",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(bottom = 24.dp)

        )

        var nameText by remember { mutableStateOf("") }
        CustomTextField(
            value = nameText,
            onValueChange = { nameText = it },
            placeholderText = "Enter your full name",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        var phoneText by remember { mutableStateOf("") }
        CustomTextField(
            value = phoneText,
            onValueChange = { phoneText = it },
            placeholderText = "Enter your phone number",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        var emailText by remember { mutableStateOf("") }
        CustomTextField(
            value = emailText,
            onValueChange = { emailText = it },
            placeholderText = "Enter your email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        var passwordText by remember { mutableStateOf("") }
        CustomTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            placeholderText = "Enter your password",
            canToggleVisibility = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        var passwordText2 by remember { mutableStateOf("") }
        CustomTextField(
            value = passwordText2,
            onValueChange = { passwordText2 = it },
            placeholderText = "Enter your password again",
            canToggleVisibility = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                // Handle login logic here
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = buttonShape
                ),
            backgroundGradient = listOf(White, White),
            shape = buttonShape,
        ) {
            Text(
                text = "Register",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Already have an account? Login now",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    onNavigateToLogin()
                }
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}