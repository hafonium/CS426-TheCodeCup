package com.example.thecodecup.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecases.auth.RegisterUseCase
import com.example.thecodecup.domain.usecases.auth.SendOtpUseCase
import com.example.thecodecup.domain.models.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val email: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val sendOtpUseCase: SendOtpUseCase
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
        address: String,
        pass: String,
        confirmPass: String
    ) {
        // Tell the UI to show a loading spinner
        _uiState.value = RegisterUiState.Loading

        // Launch a background thread so the UI doesn't freeze
        viewModelScope.launch {

            val result = registerUseCase(
                fullName = name,
                email = email,
                phone = phone,
                password = pass, confirmPass,
                address = address)

            // Update the state based on the result
            result.onSuccess {
                // The account already exists at this point. Always continue to verification;
                // that screen offers a resend action if this first delivery fails.
                sendOtpUseCase(email)
                _uiState.value = RegisterUiState.Success(email.trim())
            }.onFailure { error ->
                if (error is ApiException && error.code == EMAIL_NOT_VERIFIED) {
                    sendOtpUseCase(email)
                    _uiState.value = RegisterUiState.Success(email.trim())
                } else {
                    _uiState.value = RegisterUiState.Error(error.message ?: "Unknown error")
                }
            }
        }
    }

    // State Reset (Crucial for when the user dismisses an error dialog)
    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}

private const val EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED"
