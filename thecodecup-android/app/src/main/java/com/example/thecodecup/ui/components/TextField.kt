package com.example.thecodecup.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.R 

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    canToggleVisibility: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText, color = Color.Gray) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,

        visualTransformation = if (canToggleVisibility && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },

        trailingIcon = if (canToggleVisibility) {
            {
                val image = if (passwordVisible) {
                    painterResource(id = R.drawable.ic_visibility)
                } else {
                    painterResource(id = R.drawable.ic_visibility_off)
                }

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = image, contentDescription = description, tint = Color.Gray)
                }
            }
        } else null,

        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F7F9),
            unfocusedContainerColor = Color(0xFFF5F7F9),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    var text by remember { mutableStateOf("") }
    CustomTextField(
        value = text,
        onValueChange = { text = it },
        placeholderText = "Enter your password",
        canToggleVisibility = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}