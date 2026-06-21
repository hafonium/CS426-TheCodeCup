package com.example.thecodecup.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.ModifierLocal
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.theme.TheCodeCupTheme

@Composable
fun<T> SegmentedControl(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionContent: @Composable (T, isSelected: Boolean) -> Unit
    ) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val borderThickness = if (isSelected) 2.dp else 1.dp

                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = borderThickness,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    optionContent(option, isSelected)
                }
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SegmentedControlPreview() {
    TheCodeCupTheme {

        var currentSelection by remember { mutableStateOf("Medium") }

        SegmentedControl(
            title = "Size",
            options = listOf("Small", "Medium", "Large"),

            selectedOption = currentSelection,

            onOptionSelected = { clickedOption ->
                currentSelection = clickedOption
            },

            ) { option, isSelected ->
            Text(
                text = option,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}