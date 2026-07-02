package com.example.thecodecup.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // The State Holder (Private vs Public)
    // _uiState is private and mutable. Only the ViewModel can change it.
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    // uiState is public and read-only. The UI observes this but cannot change it.
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // The User Intent (Action triggered by the UI)
    fun onRegisterClicked(
        name: String,
        email: String,
        phone: String,
        pass: String,
        confirmPass: String
    ) {
        // Tell the UI to show a loading spinner
        _uiState.value = RegisterUiState.Loading

        // Launch a background thread so the UI doesn't freeze
        viewModelScope.launch {

            val result = registerUseCase(name, email, phone, pass, confirmPass)

            // Update the state based on the result
            result.onSuccess {
                _uiState.value = RegisterUiState.Success
            }.onFailure { error ->
                _uiState.value = RegisterUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // State Reset (Crucial for when the user dismisses an error dialog)
    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}