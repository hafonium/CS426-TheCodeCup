package com.example.thecodecup.ui.auth.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecases.auth.SendOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OtpUiState {
    object Idle : OtpUiState()
    object Loading : OtpUiState()
    object Success : OtpUiState()
    data class Error(val message: String) : OtpUiState()
    data class CodeResent(val message: String = "A new code was sent") : OtpUiState()
}

class OtpVerificationViewModel(
    private val verifyOtp: suspend (String, String) -> Result<Unit>,
    private val sendOtpUseCase: SendOtpUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<OtpUiState>(OtpUiState.Idle)
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    fun verify(email: String, code: String) {
        _uiState.value = OtpUiState.Loading
        viewModelScope.launch {
            verifyOtp(email, code).fold(
                onSuccess = { _uiState.value = OtpUiState.Success },
                onFailure = { _uiState.value = OtpUiState.Error(it.message ?: "Verification failed") }
            )
        }
    }

    fun resend(email: String) {
        // Replace any previous verification error as soon as a new request begins.
        _uiState.value = OtpUiState.Loading
        viewModelScope.launch {
            sendOtpUseCase(email).fold(
                onSuccess = { _uiState.value = OtpUiState.CodeResent() },
                onFailure = { _uiState.value = OtpUiState.Error(it.message ?: "Could not resend code") }
            )
        }
    }
}
