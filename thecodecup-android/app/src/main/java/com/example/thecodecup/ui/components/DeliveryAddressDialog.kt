package com.example.thecodecup.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeliveryAddressDialog(
    profileAddress: String,
    isLoading: Boolean,
    confirmLabel: String,
    loadingLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var useProfileAddress by remember(profileAddress) {
        mutableStateOf(profileAddress.isNotBlank())
    }
    var customAddress by remember { mutableStateOf("") }
    var showMapPicker by remember { mutableStateOf(false) }
    val address = if (useProfileAddress) profileAddress else customAddress

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delivery address") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (profileAddress.isNotBlank()) {
                    Row(
                        Modifier.fillMaxWidth().clickable { useProfileAddress = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = useProfileAddress,
                            onClick = { useProfileAddress = true }
                        )
                        Column {
                            Text("Use profile address", fontWeight = FontWeight.SemiBold)
                            Text(profileAddress, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().clickable { useProfileAddress = false },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !useProfileAddress,
                        onClick = { useProfileAddress = false }
                    )
                    Text("Enter another address", fontWeight = FontWeight.SemiBold)
                }
                if (!useProfileAddress) {
                    OutlinedTextField(
                        value = customAddress,
                        onValueChange = { customAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Delivery address") },
                        minLines = 2
                    )
                    OutlinedButton(
                        onClick = { showMapPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Map, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Select on map")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(address.trim()) },
                enabled = address.isNotBlank() && !isLoading
            ) {
                Text(if (isLoading) loadingLabel else confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
        }
    )

    if (showMapPicker) {
        MapAddressPickerDialog(
            initialAddress = customAddress,
            onDismiss = { showMapPicker = false },
            onAddressSelected = {
                customAddress = it
                useProfileAddress = false
                showMapPicker = false
            }
        )
    }
}
