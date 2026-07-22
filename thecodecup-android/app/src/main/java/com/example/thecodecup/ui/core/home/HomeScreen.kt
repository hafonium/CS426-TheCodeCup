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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { HomeBottomBar(onRewards, onOrder) },
        containerColor = Color.White
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Good morning", color = Color.Gray, fontSize = 13.sp)
                    Text(state.firstName, color = CoffeeNavy, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
                IconButton(onClick = onCart) { Icon(Icons.Outlined.ShoppingCart, "Cart", tint = CoffeeNavy) }
                IconButton(onClick = onProfile) { Icon(Icons.Outlined.Person, "Profile", tint = CoffeeNavy) }
            }
            Spacer(Modifier.height(12.dp))
            LoyaltyCard()
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
private fun LoyaltyCard() {
    Column(Modifier.fillMaxWidth().background(CoffeeBlue, RoundedCornerShape(14.dp)).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Loyalty card", color = Color.White, fontWeight = FontWeight.Medium)
            Text("4 / 8", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            repeat(8) { index -> Icon(Icons.Outlined.LocalCafe, null, tint = if (index < 4) CoffeeBlue else Color.LightGray, modifier = Modifier.size(22.dp)) }
        }
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
