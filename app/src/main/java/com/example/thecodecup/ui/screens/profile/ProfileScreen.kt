package com.example.thecodecup.ui.screens.profile

import android.R.style
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.theme.TheCodeCupTheme
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun ProfileItemRow(
    label: String,
    value: String,
    icon: ImageVector,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit $label",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp)
    ) {

        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

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