package com.example.thecodecup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeCard
import com.example.thecodecup.ui.theme.CoffeeNavy
import kotlinx.coroutines.delay

@Composable
fun Gacha(
    foods: List<FoodModel>,
    prize: FoodModel?,
    gachaponCount: Int,
    spinId: Int,
    isRequesting: Boolean,
    isSpinning: Boolean,
    onStart: () -> Unit,
    onSpinFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayedFood by remember { mutableStateOf<FoodModel?>(null) }

    LaunchedEffect(spinId) {
        if (spinId == 0 || !isSpinning || prize == null) return@LaunchedEffect
        val candidates = foods.ifEmpty { listOf(prize) }
        repeat(22) { frame ->
            displayedFood = candidates[frame % candidates.size]
            delay(65L + frame * 5L)
        }
        displayedFood = prize
        onSpinFinished()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = CoffeeBlue)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("Gacha", color = CoffeeNavy, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(
                        "$gachaponCount ${if (gachaponCount == 1) "spin" else "spins"} available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(124.dp)
                    .background(CoffeeCard, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                val food = displayedFood ?: prize
                if (food == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            null,
                            tint = CoffeeBlue,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("Spin to win a drink", color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = food.imageUrl,
                            contentDescription = food.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(86.dp)
                        )
                        Column(Modifier.padding(start = 14.dp)) {
                            Text(
                                if (isSpinning) "Spinning..." else "You won",
                                color = CoffeeBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(food.name, color = CoffeeNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onStart,
                enabled = gachaponCount > 0 && !isRequesting && !isSpinning,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBlue)
            ) {
                if (isRequesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    when {
                        isRequesting -> "Preparing your spin..."
                        isSpinning -> "Spinning..."
                        gachaponCount == 0 -> "No spins available"
                        else -> "Spin now"
                    }
                )
            }
        }
    }
}
