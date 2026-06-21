package com.example.thecodecup.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CodeCupDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
) {
    HorizontalDivider(
        modifier = modifier,
        color = color,
        thickness = thickness,
    )
}

private const val DividerAlpha = 0.12f

@Preview("default", showBackground = true)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DividerPreview() {
    MaterialTheme {
        Box(Modifier.size(height = 10.dp, width = 100.dp)) {
            CodeCupDivider(Modifier.align(Alignment.Center))
        }
    }
}