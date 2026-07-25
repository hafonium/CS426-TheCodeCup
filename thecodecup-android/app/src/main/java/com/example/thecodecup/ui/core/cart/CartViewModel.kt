package com.example.thecodecup.ui.core.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.CartItemModel
import com.example.thecodecup.domain.usecases.cart.*
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.order.CreateOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val isLoading: Boolean = false,
    val items: List<CartItemModel> = emptyList(),
    val errorMessage: String? = null,
    val message: String? = null,
    val selectedItemIds: Set<Int> = emptySet(),
    val profileAddress: String = "",
    val isCheckingOut: Boolean = false,
    val orderCreated: Boolean = false
) {
    val selectedItems get() = items.filter { it.id in selectedItemIds }
    val totalPrice: Double get() = selectedItems.sumOf { it.totalPrice }
}

class CartViewModel(
    private val getCartItems: GetCartItemsUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val updateCartItem: UpdateCartItemUseCase,
    private val updateQuantity: UpdateCartQuantityUseCase,
    private val deleteCartItem: DeleteCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val createOrder: CreateOrderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun refresh() {
        launchAction(showLoading = true) { getCartItems() }
        viewModelScope.launch {
            getCurrentUser().onSuccess { user ->
                _uiState.value = _uiState.value.copy(profileAddress = user.address)
            }
        }
    }

    fun toggleSelection(itemId: Int) {
        val ids = _uiState.value.selectedItemIds
        _uiState.value = _uiState.value.copy(
            selectedItemIds = if (itemId in ids) ids - itemId else ids + itemId
        )
    }

    fun selectAll() {
        _uiState.value = _uiState.value.copy(selectedItemIds = _uiState.value.items.map { it.id }.toSet())
    }

    fun deselectAll() {
        _uiState.value = _uiState.value.copy(selectedItemIds = emptySet())
    }

    fun checkout(address: String) {
        val selected = _uiState.value.selectedItemIds.toList()
        if (selected.isEmpty() || address.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingOut = true, errorMessage = null)
            createOrder(address, selected).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        items = _uiState.value.items.filterNot { item -> item.id in selected },
                        selectedItemIds = emptySet(),
                        isCheckingOut = false,
                        orderCreated = true
                    )
                },
                onFailure = { showError(it, loading = false); _uiState.value = _uiState.value.copy(isCheckingOut = false) }
            )
        }
    }

    fun consumeOrderCreated() {
        _uiState.value = _uiState.value.copy(orderCreated = false)
    }

    fun add(foodId: Int, quantity: Int, optionTypeIds: List<Int>) {
        viewModelScope.launch {
            addCartItem(foodId, quantity, optionTypeIds).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        message = "Added to cart",
                        errorMessage = null
                    )
                },
                onFailure = { showError(it) }
            )
        }
    }

    fun setQuantity(item: CartItemModel, quantity: Int) {
        if (quantity < 1) return
        launchItemUpdate(item.id) { updateQuantity(item.id, quantity) }
    }

    fun edit(item: CartItemModel, quantity: Int, optionTypeIds: List<Int>) {
        launchItemUpdate(item.id) { updateCartItem(item.id, item.food.id, quantity, optionTypeIds) }
    }

    fun delete(itemId: Int) {
        viewModelScope.launch {
            deleteCartItem(itemId).fold(
                onSuccess = { updatedItems ->
                    _uiState.value = _uiState.value.copy(
                        items = updatedItems,
                        errorMessage = null
                    )
                },
                onFailure = { showError(it) }
            )
        }
    }

    fun clear() {
        viewModelScope.launch {
            clearCart().fold(
                onSuccess = { updatedItems ->
                    _uiState.value = CartUiState(
                        items = updatedItems,
                        message = "Cart cleared"
                    )
                },
                onFailure = { showError(it) }
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, errorMessage = null)
    }

    private fun launchAction(showLoading: Boolean, action: suspend () -> Result<List<CartItemModel>>) {
        viewModelScope.launch {
            if (showLoading) _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            action().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = it,
                        selectedItemIds = it.map { item -> item.id }.toSet(),
                        errorMessage = null
                    )
                },
                onFailure = { showError(it, loading = false) }
            )
        }
    }

    private fun launchItemUpdate(itemId: Int, action: suspend () -> Result<CartItemModel>) {
        viewModelScope.launch {
            action().fold(
                onSuccess = { updated ->
                    _uiState.value = _uiState.value.copy(
                        items = _uiState.value.items.map { if (it.id == itemId) updated else it },
                        errorMessage = null
                    )
                },
                onFailure = { showError(it) }
            )
        }
    }

    private fun showError(error: Throwable, loading: Boolean = _uiState.value.isLoading) {
        _uiState.value = _uiState.value.copy(
            isLoading = loading,
            errorMessage = error.message ?: "Something went wrong"
        )
    }
}
