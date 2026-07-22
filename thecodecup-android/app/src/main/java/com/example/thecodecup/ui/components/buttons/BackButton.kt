package com.example.thecodecup.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.theme.CoffeeCard
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Button(
        onClick = onClick,
        modifier = modifier.size(44.dp).clip(shape).background(CoffeeCard),
        backgroundGradient = listOf(CoffeeCard, CoffeeCard),
        shape = shape,
        contentColor = CoffeeNavy,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Go back"
                ,modifier = Modifier.size(21.dp)
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
            .size(48.dp)
    )
}
