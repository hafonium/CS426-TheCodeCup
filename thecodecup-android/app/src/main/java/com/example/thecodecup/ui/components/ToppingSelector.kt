package com.example.thecodecup.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image // <-- Added standard Image component
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // <-- Added to load local resources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thecodecup.R
import com.example.thecodecup.domain.models.ToppingModel
import com.example.thecodecup.ui.theme.TheCodeCupTheme

@Composable
fun ToppingSelector(
    title: String,
    toppings: List<ToppingModel>,
    onToppingToggled: (ToppingModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        toppings.forEach { topping ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToppingToggled(topping) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Image(
                    painter = painterResource(id = topping.imageResId),
                    contentDescription = topping.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = topping.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (topping.isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = topping.formattedPrice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Checkbox(
                    checked = topping.isSelected,
                    onCheckedChange = { onToppingToggled(topping) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun ToppingSelectorPreview() {
    TheCodeCupTheme {
        ToppingSelector(
            title = "Toppings",
            toppings = listOf(
                ToppingModel(1, "Mushrooms", 0.75f, "Topping", R.drawable.black_boba, false),
            ),
            onToppingToggled = {}
        )
    }
}