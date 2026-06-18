package com.example.thecodecup.ui.auth.login

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
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToWelcome : () -> Unit = { },
    onNavigateToRegister : () -> Unit = { }
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
            text = "Welcome back! Glad to see you, Again!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(bottom = 24.dp)

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

        Text(
            text = "Forgot your password?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    // Handle forgot password logic here
                }
                .align(Alignment.End)
        )

        Button(
            onClick = {
                // Handle login logic here
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
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
                text = "Login",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Don't have an account? Sign up",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    onNavigateToRegister()
                }
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview("default", showBackground = true)
@Preview("dark theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    ScreenWrapper {
        LoginScreen(onNavigateToWelcome = {  })
    }
}