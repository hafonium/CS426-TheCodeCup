package com.example.thecodecup.ui.core.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.models.FoodOptionTypeModel
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeCard
import com.example.thecodecup.ui.theme.CoffeeNavy
import com.example.thecodecup.ui.core.cart.CartViewModel

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cartState.message, cartState.errorMessage) {
        (cartState.message ?: cartState.errorMessage)?.let {
            snackbarHostState.showSnackbar(it)
            cartViewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onNavigateBack)
                Text(
                    "Details",
                    Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = CoffeeNavy,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onNavigateToCart) {
                    Icon(Icons.Outlined.ShoppingCart, "View cart", tint = CoffeeNavy)
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator(color = CoffeeBlue)
                state.errorMessage != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::refresh) { Text("Try again") }
                }
                state.food != null -> DetailContent(state.food!!, cartViewModel::add)
            }
        }
    }
}

@Composable
private fun DetailContent(food: FoodModel, onAddToCart: (Int, Int, List<Int>) -> Unit) {
    var quantity by rememberSaveable(food.id) { mutableIntStateOf(1) }
    var isDescriptionExpanded by rememberSaveable(food.id) { mutableStateOf(false) }
    var selections by remember(food.id) {
        mutableStateOf(food.options.mapNotNull { option ->
            option.types.firstOrNull()?.let { option.id to it }
        }.toMap())
    }
    val total = (food.price + selections.values.sumOf { it.price }) * quantity

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
    ) {
        AsyncImage(
            model = food.imageUrl,
            contentDescription = food.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(190.dp).background(CoffeeCard, RoundedCornerShape(16.dp)).padding(16.dp)
        )
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(food.name, color = CoffeeNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${food.rewardPoint} reward points",
                    color = CoffeeBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (food.description.isNotBlank()) {
                    var isDescriptionOverflowing by remember(food.id, food.description) {
                        mutableStateOf(false)
                    }
                    Text(
                        text = food.description,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            if (!isDescriptionExpanded) {
                                isDescriptionOverflowing = it.hasVisualOverflow
                            }
                        }
                    )
                    if (isDescriptionOverflowing || isDescriptionExpanded) {
                        Text(
                            text = if (isDescriptionExpanded) "Less" else "More",
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clickable { isDescriptionExpanded = !isDescriptionExpanded },
                            color = CoffeeBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            QuantitySelector(
                quantity = quantity,
                onDecrease = { if (quantity > 1) quantity-- },
                onIncrease = { quantity++ }
            )
        }
        food.options.forEach { option ->
            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = CoffeeCard)
            Text(
                text = option.name,
                modifier = Modifier.fillMaxWidth(),
                color = CoffeeNavy,
                fontWeight = FontWeight.SemiBold,
                softWrap = true
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                option.types.forEach { type ->
                    OptionChip(
                        type = type,
                        selected = selections[option.id] == type,
                        onClick = { selections = selections + (option.id to type) }
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount", color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
            Text("$${"%.2f".format(total)}", color = CoffeeNavy, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = { onAddToCart(food.id, quantity, selections.values.mapNotNull { it.id }) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CoffeeBlue)
        ) {
            Text("Add to cart", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun QuantitySelector(quantity: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Row(
        Modifier.background(CoffeeCard, RoundedCornerShape(50)).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDecrease, enabled = quantity > 1, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Outlined.Remove, "Decrease quantity", modifier = Modifier.size(16.dp))
        }
        Text(quantity.toString(), color = CoffeeNavy, fontWeight = FontWeight.Bold)
        IconButton(onClick = onIncrease, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Outlined.Add, "Increase quantity", modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun OptionChip(type: FoodOptionTypeModel, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) CoffeeBlue else CoffeeCard
    ) {
        Text(
            text = if (type.price == 0.0) type.name else "${type.name}  +$${"%.2f".format(type.price)}",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            color = if (selected) Color.White else CoffeeNavy,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Clip
        )
    }
}
