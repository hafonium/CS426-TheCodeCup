package com.example.thecodecup.ui.core.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.home.GetFoodsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val firstName: String = "",
    val foods: List<FoodModel> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFoodsUseCase: GetFoodsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val userResult = async { getCurrentUserUseCase() }
            val foodResult = async { getFoodsUseCase() }
            val user = userResult.await()
            val foods = foodResult.await()
            _uiState.value = HomeUiState(
                isLoading = false,
                firstName = user.getOrNull()?.fullName?.trim()?.substringBefore(" ")?.takeIf { it.isNotBlank() } ?: "there",
                foods = foods.getOrDefault(emptyList()),
                errorMessage = foods.exceptionOrNull()?.message
            )
        }
    }
}
