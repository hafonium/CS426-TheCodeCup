package com.example.thecodecup.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    modifier: Modifier = Modifier,
    label: String = "Select Date",
    onDateSelected: (Long?) -> Unit
) {
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    val displayedDate = selectedDateMillis?.let {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        formatter.format(Date(it))
    } ?: ""

    OutlinedTextField(
        value = displayedDate,
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text("MM/DD/YYYY") },
        readOnly = true,
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Open Calendar")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDateMillis) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    onDateSelected(datePickerState.selectedDateMillis)
                    showModal = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview
@Composable
private fun DatePickerFieldPreview() {
    DatePickerField(onDateSelected = { })
}