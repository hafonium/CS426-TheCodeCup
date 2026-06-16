package com.example.thecodecup.ui.components.buttons

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        backgroundGradient = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(percent = 15),
        contentColor = MaterialTheme.colorScheme.primary,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Go back"
            )
        }
    }
}

@Preview
@Composable
fun BackButtonPreview() {
    BackButton(
        onClick = { },
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            .size(48.dp)
    )
}