package com.example.thecodecup.ui.core.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.OrderModel
import com.example.thecodecup.domain.usecases.order.GetOrdersUseCase
import com.example.thecodecup.domain.usecases.order.CompleteOrderUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val isLoading: Boolean = false,
    val ongoing: List<OrderModel> = emptyList(),
    val completed: List<OrderModel> = emptyList(),
    val errorMessage: String? = null,
    val completingOrderIds: Set<Int> = emptySet()
)

class OrderViewModel(
    private val getOrders: GetOrdersUseCase,
    private val completeOrder: CompleteOrderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val ongoing = async { getOrders("ongoing") }
            val completed = async { getOrders("completed") }
            val first = ongoing.await()
            val second = completed.await()
            val error = first.exceptionOrNull() ?: second.exceptionOrNull()
            _uiState.value = OrderUiState(
                ongoing = first.getOrDefault(emptyList()),
                completed = second.getOrDefault(emptyList()),
                errorMessage = error?.message
            )
        }
    }

    fun complete(order: OrderModel) {
        if (order.id in _uiState.value.completingOrderIds) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                completingOrderIds = _uiState.value.completingOrderIds + order.id,
                errorMessage = null
            )
            completeOrder(order.id).fold(
                onSuccess = { completedOrder ->
                    _uiState.value = _uiState.value.copy(
                        ongoing = _uiState.value.ongoing.filterNot { it.id == order.id },
                        completed = listOf(completedOrder) + _uiState.value.completed.filterNot { it.id == order.id },
                        completingOrderIds = _uiState.value.completingOrderIds - order.id
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        completingOrderIds = _uiState.value.completingOrderIds - order.id,
                        errorMessage = it.message ?: "Unable to complete order"
                    )
                }
            )
        }
    }
}
