package com.example.thecodecup.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor // <-- Added import
import androidx.compose.runtime.CompositionLocalProvider // <-- Added import
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.example.thecodecup.ui.components.Surface
import com.example.thecodecup.ui.theme.DarkBrown
import com.example.thecodecup.ui.theme.Gray
import com.example.thecodecup.ui.theme.LightBrown
import com.example.thecodecup.ui.theme.TheCodeCupTheme
import com.example.thecodecup.ui.theme.White

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonShape,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = listOf(LightBrown, DarkBrown),
    disabledBackgroundGradient: List<Color> = listOf(Gray, White),
    contentColor: Color = Color.White,
    disabledContentColor: Color = Color.Gray,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    val targetContentColor = if (enabled) contentColor else disabledContentColor

    Surface(
        shape = shape,
        color = Color.Transparent,
        contentColor = targetContentColor,
        border = border,
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = if (enabled) backgroundGradient else disabledBackgroundGradient,
                ),
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
            )
    ) {
        CompositionLocalProvider(LocalContentColor provides targetContentColor) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight,
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

val ButtonShape = RoundedCornerShape(percent = 50)

@Preview("Button Preview")
@Composable
private fun CodeCupButtonPreview() {
    TheCodeCupTheme {
        Button(onClick = {}) {
            Text(text = "Add to cart")
        }
    }
}