package com.example.thecodecup.ui.core.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome : () -> Unit = { }
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            BackButton(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(percent = 15)
                    )
                    .size(48.dp)
            )

            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        ProfileItemRow(
            label = "Full name",
            value = "Anderson",
            icon = Icons.Outlined.Person,
            onEditClick = { /* TODO: Open Name Editor */ }
        )

        ProfileItemRow(
            label = "Phone number",
            value = "+60134589525",
            icon = Icons.Outlined.Phone,
            onEditClick = { /* TODO: Open Phone Editor */ }
        )

        ProfileItemRow(
            label = "Email",
            value = "Anderson@email.com",
            icon = Icons.Outlined.Email,
            onEditClick = { /* TODO: Open Email Editor */ }
        )

        ProfileItemRow(
            label = "Address",
            value = "3 Addersion Court\nChino Hills, HO56824, United State",
            icon = Icons.Outlined.LocationOn,
            onEditClick = { /* TODO: Open Address Editor */ }
        )
    }
}

@Preview("default", showBackground = true)
@Preview("dark theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    ScreenWrapper {
        ProfileScreen()
    }
}