package com.example.thecodecup.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onLoginClicked(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            result.onSuccess {
                _uiState.value = LoginUiState.Success
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}