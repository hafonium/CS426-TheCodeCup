package com.example.thecodecup.ui.core.rewards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.thecodecup.domain.models.RedeemRewardModel
import com.example.thecodecup.ui.components.DeliveryAddressDialog
import com.example.thecodecup.ui.components.RewardSuccessDialog
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun RedeemRewardScreen(
    viewModel: RedeemRewardViewModel,
    onBack: () -> Unit,
    onFood: (Int) -> Unit,
    onRedeemed: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedReward by remember { mutableStateOf<RedeemRewardModel?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.message, state.errorMessage) {
        state.message?.let {
            successMessage = it
            onRedeemed()
            viewModel.clearFeedback()
            return@LaunchedEffect
        }
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack)
                Text("Redeem", color = CoffeeNavy, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                Text("${state.totalPoints} pts", color = CoffeeBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CoffeeBlue)
            }
            state.rewards.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No drinks are available to redeem", color = CoffeeNavy)
                TextButton(onClick = viewModel::refresh) { Text("Refresh") }
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(state.rewards, key = { it.id }) { reward ->
                    RedeemRewardRow(
                        reward = reward,
                        canAfford = state.totalPoints >= reward.requiredPoint,
                        isLoading = state.redeemingRewardId == reward.id,
                        onFood = onFood,
                        onRedeem = { selectedReward = reward }
                    )
                }
            }
        }
    }

    selectedReward?.let { reward ->
        DeliveryAddressDialog(
            profileAddress = state.profileAddress,
            isLoading = state.redeemingRewardId == reward.id,
            confirmLabel = "Redeem ${reward.requiredPoint} pts",
            loadingLabel = "Redeeming...",
            onDismiss = { if (state.redeemingRewardId == null) selectedReward = null },
            onConfirm = {
                viewModel.redeem(reward, it)
                selectedReward = null
            }
        )
    }

    successMessage?.let { message ->
        RewardSuccessDialog(
            title = "Drink redeemed!",
            message = message,
            onDismiss = { successMessage = null }
        )
    }
}

@Composable
private fun RedeemRewardRow(
    reward: RedeemRewardModel,
    canAfford: Boolean,
    isLoading: Boolean,
    onFood: (Int) -> Unit,
    onRedeem: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable { onFood(reward.food.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = reward.food.imageUrl,
            contentDescription = reward.food.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(72.dp)
        )
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(reward.food.name, color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
            Text("Valid until ${formatExpiration(reward.expirationTime)}", color = Color.Gray, fontSize = 11.sp)
        }
        Button(
            onClick = onRedeem,
            enabled = canAfford && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = CoffeeBlue),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(if (isLoading) "..." else "${reward.requiredPoint} pts", fontSize = 11.sp)
        }
    }
}

private fun formatExpiration(value: String): String =
    value.substringBefore('T').ifBlank { value }
