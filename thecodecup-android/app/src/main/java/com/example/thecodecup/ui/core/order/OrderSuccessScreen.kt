package com.example.thecodecup.ui.core.order

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun OrderSuccessScreen(onTrackOrder: () -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.CheckCircle, null, Modifier.size(112.dp), tint = CoffeeNavy)
        Spacer(Modifier.height(22.dp))
        Text("Order Success", color = CoffeeNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Your order has been placed successfully.", color = Color.Gray, fontSize = 13.sp)
        Text("For more details, go to my orders.", color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(54.dp))
        Button(
            onClick = onTrackOrder,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CoffeeBlue)
        ) { Text("Track My Order") }
    }
}
