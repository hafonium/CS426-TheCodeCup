package com.example.thecodecup.ui.core.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.usecases.details.GetFoodDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val food: FoodModel? = null,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val foodId: Int,
    private val getFoodDetailsUseCase: GetFoodDetailsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = DetailUiState()
            getFoodDetailsUseCase(foodId).fold(
                onSuccess = { _uiState.value = DetailUiState(isLoading = false, food = it) },
                onFailure = {
                    _uiState.value = DetailUiState(
                        isLoading = false,
                        errorMessage = it.message ?: "Unable to load food details"
                    )
                }
            )
        }
    }
}
