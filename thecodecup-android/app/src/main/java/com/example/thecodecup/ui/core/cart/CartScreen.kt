package com.example.thecodecup.ui.core.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.example.thecodecup.domain.models.CartItemModel
import com.example.thecodecup.domain.models.FoodOptionTypeModel
import com.example.thecodecup.ui.components.buttons.BackButton
import com.example.thecodecup.ui.theme.CoffeeBlue
import com.example.thecodecup.ui.theme.CoffeeCard
import com.example.thecodecup.ui.theme.CoffeeNavy

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<CartItemModel?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.refresh() }

    LaunchedEffect(state.errorMessage, state.message) {
        (state.errorMessage ?: state.message)?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onNavigateBack)
                Text("My Cart", Modifier.weight(1f), color = CoffeeNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (state.items.isNotEmpty()) TextButton(onClick = viewModel::clear) { Text("Clear", color = MaterialTheme.colorScheme.error) }
            }
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Total Price", color = Color.Gray, fontSize = 12.sp)
                        Text("$${"%.2f".format(state.totalPrice)}", color = CoffeeNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = CoffeeBlue)) {
                        Icon(Icons.Outlined.ShoppingCartCheckout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Checkout")
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = CoffeeBlue)
                state.items.isEmpty() -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.ShoppingCart, null, Modifier.size(52.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(12.dp))
                    Text("Your cart is empty", color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                    Text("Add something delicious to get started", color = Color.Gray, fontSize = 13.sp)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                            onDelete = viewModel::delete,
                            onEdit = { editing = item },
                            onDetails = onNavigateToDetails,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    editing?.let { item ->
        EditCartItemDialog(
            item = item,
            onDismiss = { editing = null },
            onSave = { quantity, selections ->
                viewModel.edit(item, quantity, selections.mapNotNull { it.id })
                editing = null
            }
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItemModel,
    onDelete: (Int) -> Unit,
    onEdit: () -> Unit,
    onDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) onDelete(item.id)
            it == SwipeToDismissBoxValue.EndToStart
        }
    )
    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(16.dp)).padding(20.dp),
                contentAlignment = Alignment.CenterEnd
            ) { Icon(Icons.Outlined.Delete, "Delete item", tint = MaterialTheme.colorScheme.error) }
        }
    ) {
        Row(
            Modifier.fillMaxWidth().background(CoffeeCard, RoundedCornerShape(16.dp))
                .clickable { onDetails(item.food.id) }.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.food.imageUrl, contentDescription = item.food.name, contentScale = ContentScale.Fit,
                modifier = Modifier.size(76.dp).padding(4.dp)
            )
            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text(item.food.name, color = CoffeeNavy, fontWeight = FontWeight.Bold)
                Text(item.selectedOptionTypes.joinToString(" | ") { it.name }.ifBlank { "Standard" }, color = Color.Gray, fontSize = 11.sp)
                Text("x ${item.quantity}", color = CoffeeNavy, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${"%.2f".format(item.totalPrice)}", color = CoffeeNavy, fontWeight = FontWeight.Bold)
                IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Outlined.Edit, "Edit item", tint = CoffeeBlue)
                }
            }
        }
    }
}

@Composable
private fun EditCartItemDialog(
    item: CartItemModel,
    onDismiss: () -> Unit,
    onSave: (Int, List<FoodOptionTypeModel>) -> Unit
) {
    var quantity by remember(item.id) { mutableIntStateOf(item.quantity) }
    var selections by remember(item.id) {
        mutableStateOf(item.food.options.mapNotNull { option ->
            (option.types.firstOrNull { candidate -> item.selectedOptionTypes.any { it.id == candidate.id } }
                ?: option.types.firstOrNull())?.let { option.id to it }
        }.toMap())
    }
    val totalPrice = (item.food.price + selections.values.sumOf { it.price }) * quantity
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${item.food.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantity", Modifier.weight(1f))
                    IconButton(onClick = { if (quantity > 1) quantity-- }) { Icon(Icons.Outlined.Remove, "Decrease") }
                    Text(quantity.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { quantity++ }) { Icon(Icons.Outlined.Add, "Increase") }
                }
                item.food.options.forEach { option ->
                    Text(option.name, color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        option.types.forEach { type ->
                            FilterChip(
                                selected = selections[option.id]?.id == type.id,
                                onClick = { selections = selections + (option.id to type) },
                                label = { Text(type.name) }
                            )
                        }
                    }
                }
                HorizontalDivider(color = CoffeeCard)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Price", color = CoffeeNavy, fontWeight = FontWeight.SemiBold)
                    Text(
                        "$${"%.2f".format(totalPrice)}",
                        color = CoffeeNavy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(quantity, selections.values.toList()) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
