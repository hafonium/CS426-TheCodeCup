package com.example.thecodecup.ui.auth.welcome

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.components.buttons.Button
import com.example.thecodecup.ui.theme.White
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val buttonShape = RoundedCornerShape(percent = 15)
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                // Navigate to LoginScreen
                onNavigateToLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
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

        Button(
            onClick = {
                // Navigate to RegisterScreen
                onNavigateToRegister()
            },
            modifier = Modifier
                .fillMaxWidth(),
            backgroundGradient = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            shape = buttonShape,
        ) {
            Text(
                text = "Register",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview("default", showBackground = true)
@Preview("dark theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenPreview() {
    ScreenWrapper {
        WelcomeScreen(
            onNavigateToLogin = { },
            onNavigateToRegister = { }
        )
    }
}