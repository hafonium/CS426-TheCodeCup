package com.example.thecodecup.ui.screens.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.components.Button
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToWelcome : () -> Unit = { }
) {
    val buttonShape = RoundedCornerShape(percent = 15)
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
    ) {
        // Back button
        Button(
            onClick = {
                onNavigateToWelcome()
            },
            modifier = Modifier
                .size(48.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = buttonShape
                ),
            backgroundGradient = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface),
            shape = buttonShape,
            contentColor = MaterialTheme.colorScheme.primary,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Go back",
            )
        }

        Text(
            text = "Welcome back! Glad to see you, Again!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp)
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