package com.example.thecodecup.ui.core.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thecodecup.domain.models.GainedRewardModel
import com.example.thecodecup.ui.components.LoyaltyCard
import com.example.thecodecup.ui.components.DeliveryAddressDialog
import com.example.thecodecup.ui.components.Gacha
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun RewardScreen(
    viewModel: RewardViewModel,
    onHome: () -> Unit,
    onOrder: () -> Unit,
    onRedeem: () -> Unit,
    onFood: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(false, onHome, { Icon(Icons.Outlined.Home, "Home") }, label = { Text("Home") })
                NavigationBarItem(true, {}, { Icon(Icons.Outlined.CardGiftcard, "Rewards") }, label = { Text("Rewards") })
                NavigationBarItem(false, onOrder, { Icon(Icons.AutoMirrored.Outlined.ReceiptLong, "Order") }, label = { Text("Order") })
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CoffeeBlue)
            }
            state.errorMessage != null && state.history.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = viewModel::refresh) { Text("Try again") }
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Rewards", color = CoffeeNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(18.dp))
                    LoyaltyCard(state.loyaltyCount)
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().background(CoffeeBlue, RoundedCornerShape(14.dp)).padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("My Points:", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
                            Text(state.totalPoints.toString(), color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onRedeem,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = .18f))
                        ) { Text("Redeem drinks", color = Color.White, fontSize = 12.sp) }
                    }
                }
                item {
                    Gacha(
                        foods = state.foods,
                        prize = state.gachaPrize,
                        gachaponCount = state.gachaponCount,
                        spinId = state.gachaSpinId,
                        isRequesting = state.isRequestingGacha,
                        isSpinning = state.isSpinningGacha,
                        onStart = { showAddressDialog = true },
                        onSpinFinished = viewModel::finishGachaSpin
                    )
                }
                item {
                    Text("History Rewards", color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                }
                if (state.history.isEmpty()) {
                    item { Text("No reward history yet", color = Color.Gray, modifier = Modifier.padding(vertical = 24.dp)) }
                } else {
                    items(state.history, key = { it.id }) { reward ->
                        RewardHistoryRow(reward, onFood)
                        HorizontalDivider(color = Color(0xFFF1F1F1))
                    }
                }
            }
        }
    }

    if (showAddressDialog) {
        DeliveryAddressDialog(
            profileAddress = state.profileAddress,
            isLoading = state.isRequestingGacha,
            confirmLabel = "Confirm and spin",
            loadingLabel = "Preparing...",
            onDismiss = {
                if (!state.isRequestingGacha) showAddressDialog = false
            },
            onConfirm = {
                showAddressDialog = false
                viewModel.useGachapon(it)
            }
        )
    }
}

@Composable
private fun RewardHistoryRow(reward: GainedRewardModel, onFood: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onFood(reward.food.id) }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(reward.food.name, color = CoffeeNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(formatRewardDate(reward.createdAt), color = Color.LightGray, fontSize = 11.sp)
        }
        Text("+ ${reward.gainedPoint} Pts", color = CoffeeNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

private fun formatRewardDate(value: String): String =
    value.replace('T', ' ').replace("Z", "").take(16).ifBlank { value }
