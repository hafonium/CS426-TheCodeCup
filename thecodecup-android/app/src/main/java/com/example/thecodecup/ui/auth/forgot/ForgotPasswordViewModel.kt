package com.example.thecodecup.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecases.auth.ChangeForgotPasswordUseCase
import com.example.thecodecup.domain.usecases.auth.SendOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object Success : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}

class ForgotPasswordViewModel(
    private val sendOtpUseCase: SendOtpUseCase? = null,
    private val changePasswordUseCase: ChangeForgotPasswordUseCase? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun sendCode(email: String) = runRequest {
        sendOtpUseCase?.invoke(email)
            ?: Result.failure(Exception("Password recovery is unavailable"))
    }

    fun changePassword(email: String, password: String, confirmation: String) = runRequest {
        changePasswordUseCase?.invoke(email, password, confirmation)
            ?: Result.failure(Exception("Password recovery is unavailable"))
    }

    private fun runRequest(request: suspend () -> Result<Unit>) {
        _uiState.value = ForgotPasswordUiState.Loading
        viewModelScope.launch {
            request().fold(
                onSuccess = { _uiState.value = ForgotPasswordUiState.Success },
                onFailure = {
                    _uiState.value = ForgotPasswordUiState.Error(
                        it.message ?: "Password recovery failed"
                    )
                }
            )
        }
    }
}
