package com.example.thecodecup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeCard

@Composable
fun LoyaltyCard(
    loyaltyCount: Int,
    modifier: Modifier = Modifier
) {
    val visibleCount = loyaltyCount.coerceIn(0, 8)
    Column(
        modifier
            .fillMaxWidth()
            .background(CoffeeBlue, RoundedCornerShape(14.dp))
            .padding(18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Loyalty card", color = Color.White, fontWeight = FontWeight.Medium)
            Text("$visibleCount / 8", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .background(CoffeeCard, RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(8) { index ->
                Icon(
                    Icons.Outlined.LocalCafe,
                    contentDescription = null,
                    tint = if (index < visibleCount) CoffeeBlue else Color.LightGray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
