package com.example.thecodecup.ui.core.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeNavy
import com.example.thecodecup.ui.components.LoyaltyCard
import java.util.Calendar
import java.util.TimeZone
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onNavigateToOrder: () -> Unit = {},
    onNavigateToDetails: (Int) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(state, modifier, viewModel::refresh, onNavigateToProfile, onNavigateToCart,
        onNavigateToRewards, onNavigateToOrder, onNavigateToDetails)
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    modifier: Modifier,
    onRetry: () -> Unit,
    onProfile: () -> Unit,
    onCart: () -> Unit,
    onRewards: () -> Unit,
    onOrder: () -> Unit,
    onDetails: (Int) -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val greeting by vietnamGreeting()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { HomeBottomBar(onRewards, onOrder) },
        containerColor = Color.White
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting, color = Color.Gray, fontSize = 13.sp)
                    Text(state.firstName, color = CoffeeNavy, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
                IconButton(onClick = onCart) { Icon(Icons.Outlined.ShoppingCart, "Cart", tint = CoffeeNavy) }
                IconButton(onClick = onProfile) { Icon(Icons.Outlined.Person, "Profile", tint = CoffeeNavy) }
            }
            Spacer(Modifier.height(12.dp))
            LoyaltyCard(state.loyaltyCount)
            Spacer(Modifier.height(24.dp))
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CoffeeBlue) }
                state.errorMessage != null -> Column(Modifier.fillMaxWidth().padding(top = 36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.errorMessage, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRetry) { Text("Try again") }
                }
                state.foods.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No food available") }
                else -> FoodCategoryBrowser(
                    foods = state.foods,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onBackToCategories = { selectedCategory = null },
                    onFoodSelected = onDetails
                )
            }
        }
    }
}

@Composable
private fun vietnamGreeting() = produceState(initialValue = currentVietnamGreeting()) {
    while (true) {
        value = currentVietnamGreeting()
        delay(60_000)
    }
}

private fun currentVietnamGreeting(): String {
    val hour = Calendar
        .getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
        .get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}

@Composable
private fun HomeBottomBar(onRewards: () -> Unit, onOrder: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Outlined.Home, "Home") }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = onRewards, icon = { Icon(Icons.Outlined.CardGiftcard, "Rewards") }, label = { Text("Rewards") })
        NavigationBarItem(selected = false, onClick = onOrder, icon = { Icon(Icons.AutoMirrored.Outlined.ReceiptLong, "Order") }, label = { Text("Order") })
    }
}
