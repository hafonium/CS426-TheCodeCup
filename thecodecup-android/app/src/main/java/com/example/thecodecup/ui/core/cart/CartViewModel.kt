package com.example.thecodecup.ui.core.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.CartItemModel
import com.example.thecodecup.domain.usecases.cart.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val isLoading: Boolean = false,
    val items: List<CartItemModel> = emptyList(),
    val errorMessage: String? = null,
    val message: String? = null
) {
    val totalPrice: Double get() = items.sumOf { it.totalPrice }
}

class CartViewModel(
    private val getCartItems: GetCartItemsUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val updateCartItem: UpdateCartItemUseCase,
    private val updateQuantity: UpdateCartQuantityUseCase,
    private val deleteCartItem: DeleteCartItemUseCase,
    private val clearCart: ClearCartUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun refresh() = launchAction(showLoading = true) { getCartItems() }

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
                onSuccess = { _uiState.value = CartUiState(items = it) },
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
