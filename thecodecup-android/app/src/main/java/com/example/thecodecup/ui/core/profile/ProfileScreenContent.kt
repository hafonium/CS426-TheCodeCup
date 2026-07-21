package com.example.thecodecup.ui.core.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.components.buttons.Button
import com.example.thecodecup.ui.theme.White

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onLogoutClicked: () -> Unit,
    fetchCurrentUser: () -> Unit,
    onUpdateClicked: (String, String, String, String, String, String?, String?, String?) -> Unit = { _, _, _, _, _, _, _, _ -> },
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = { }
) {
    val buttonShape = RoundedCornerShape(percent = 15)

    // Initial Loading State
    if (uiState is ProfileUiState.Loading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    // Extract user from LoggedIn (or Error if available)
    val currentUser = when (uiState) {
        is ProfileUiState.LoggedIn -> uiState.user
        is ProfileUiState.Error -> uiState.user // Works if Error retains user, otherwise null
        else -> null
    }

    if (currentUser != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            // Top Bar
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
                            shape = buttonShape
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

            // Item Rows
            ProfileItemRow(
                label = "Full name",
                value = currentUser.fullName,
                icon = Icons.Outlined.Person,
                onSave = {
                    onUpdateClicked(currentUser.email, it, currentUser.phoneNumber, currentUser.avatarUrl ?: "", currentUser.address, null, null, null)
                }
            )

            ProfileItemRow(
                label = "Phone number",
                value = currentUser.phoneNumber,
                icon = Icons.Outlined.Phone,
                onSave = {
                    onUpdateClicked(currentUser.email, currentUser.fullName, it, currentUser.avatarUrl ?: "", currentUser.address, null, null, null)
                }
            )

            ProfileItemRow(
                label = "Email",
                value = currentUser.email,
                icon = Icons.Outlined.Email,
                onSave = {
                    onUpdateClicked(it, currentUser.fullName, currentUser.phoneNumber, currentUser.avatarUrl ?: "", currentUser.address, null, null, null)
                }
            )

            ProfileItemRow(
                label = "Address",
                value = currentUser.address,
                icon = Icons.Outlined.LocationOn,
                onSave = {
                    onUpdateClicked(currentUser.email, currentUser.fullName, currentUser.phoneNumber, currentUser.avatarUrl ?: "", it, null, null, null)
                }
            )

            ProfilePasswordItemRow(
                passwordUpdateVersion = when (uiState) {
                    is ProfileUiState.LoggedIn -> uiState.passwordUpdateVersion
                    is ProfileUiState.Error -> uiState.passwordUpdateVersion
                    else -> 0
                },
                onSave = { currentPassword, newPassword, confirmNewPassword ->
                    onUpdateClicked(
                        currentUser.email,
                        currentUser.fullName,
                        currentUser.phoneNumber,
                        currentUser.avatarUrl ?: "",
                        currentUser.address,
                        currentPassword,
                        newPassword,
                        confirmNewPassword
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Display Error Message if in Error state
            if (uiState is ProfileUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            // Logout Button
            Button(
                onClick = onLogoutClicked,
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
                    text = "Logout",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
