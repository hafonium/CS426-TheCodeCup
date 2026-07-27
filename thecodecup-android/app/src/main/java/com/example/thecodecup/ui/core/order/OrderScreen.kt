package com.example.thecodecup.ui.core.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thecodecup.domain.models.OrderModel
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeCard
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun OrderScreen(
    viewModel: OrderViewModel,
    onHome: () -> Unit,
    onRewards: () -> Unit,
    onFood: (Int) -> Unit,
    onOrderCompleted: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var completedSelected by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) { viewModel.refresh() }
    LaunchedEffect(state.completionMessage) {
        state.completionMessage?.let { message ->
            onOrderCompleted()
            snackbarHostState.showSnackbar(message)
            viewModel.consumeCompletionMessage()
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(false, onHome, { Icon(Icons.Outlined.Home, "Home") }, label = { Text("Home") })
                NavigationBarItem(false, onRewards, { Icon(Icons.Outlined.CardGiftcard, "Rewards") }, label = { Text("Rewards") })
                NavigationBarItem(true, {}, { Icon(Icons.AutoMirrored.Outlined.ReceiptLong, "Orders") }, label = { Text("Orders") })
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Text("My Orders", color = CoffeeNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            TabRow(selectedTabIndex = if (completedSelected) 1 else 0, containerColor = Color.White, contentColor = CoffeeBlue) {
                Tab(!completedSelected, { completedSelected = false }, text = { Text("Ongoing") })
                Tab(completedSelected, { completedSelected = true }, text = { Text("Completed") })
            }
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CoffeeBlue) }
                state.errorMessage != null -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(state.errorMessage ?: "Unable to load orders", color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::refresh) { Text("Try again") }
                }
                else -> {
                    val orders = if (completedSelected) state.completed else state.ongoing
                    if (orders.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (completedSelected) "No completed orders yet" else "No ongoing orders", color = Color.Gray)
                    } else LazyColumn(
                        contentPadding = PaddingValues(vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders, key = { it.id }) {
                            OrderCard(
                                order = it,
                                isOngoing = !completedSelected,
                                isCompleting = it.id in state.completingOrderIds,
                                onComplete = { viewModel.complete(it) },
                                onFood = onFood
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderModel,
    isOngoing: Boolean,
    isCompleting: Boolean,
    onComplete: () -> Unit,
    onFood: (Int) -> Unit
) {
    var expanded by rememberSaveable(order.id) { mutableStateOf(false) }
    val hasRewardItem = order.items.any { it.isRewardItem() }
    Card(colors = CardDefaults.cardColors(containerColor = CoffeeCard), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    order.createdAt.replace("T", " ").take(16),
                    color = CoffeeNavy,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                if (hasRewardItem) {
                    FreePrice(
                        originalPrice = "$${"%.2f".format(order.totalPrice)}",
                        color = CoffeeNavy,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "$${"%.2f".format(order.totalPrice)}",
                        color = CoffeeNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                order.items.joinToString(", ") { "${it.quantity}× ${it.name}" },
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                color = CoffeeNavy,
                fontSize = 13.sp
            )
            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 8.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Outlined.LocationOn, "Delivery address", tint = CoffeeBlue)
                        Column(Modifier.padding(start = 10.dp)) {
                            Text("Delivery address", color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                            Text(order.address, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    order.items.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onFood(item.foodId) }.padding(vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.LocalCafe, null, tint = CoffeeBlue)
                            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                                Text(item.name, color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                                Text(item.description, color = Color.Gray, fontSize = 11.sp, maxLines = 2)
                            }
                            if (item.isRewardItem()) {
                                FreePrice(
                                    originalPrice = "${item.quantity} × $${"%.2f".format(item.price)}",
                                    fontSize = 12.sp
                                )
                            } else {
                                Text("${item.quantity} × $${"%.2f".format(item.price)}", fontSize = 12.sp)
                            }
                            Icon(Icons.Outlined.ChevronRight, "View food")
                        }
                    }
                }
            }
            TextButton(onClick = { expanded = !expanded }, modifier = Modifier.align(Alignment.End)) {
                Text(if (expanded) "Show less" else "View order information")
                Icon(if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null)
            }
            if (isOngoing) {
                Button(
                    onClick = onComplete,
                    enabled = !isCompleting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeeBlue)
                ) {
                    if (isCompleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isCompleting) "Completing..." else "Complete order")
                }
            }
        }
    }
}

@Composable
private fun FreePrice(
    originalPrice: String,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontWeight: FontWeight? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = originalPrice,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textDecoration = TextDecoration.LineThrough
        )
        Text(
            text = "$0.00",
            color = CoffeeBlue,
            fontSize = fontSize,
            fontWeight = fontWeight ?: FontWeight.SemiBold
        )
    }
}

private fun com.example.thecodecup.domain.models.OrderItemModel.isRewardItem(): Boolean =
    description.trim().equals("Gachapon Reward", ignoreCase = true) ||
        description.trim().equals("Redeemed Reward", ignoreCase = true)
