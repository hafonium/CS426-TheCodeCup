package com.example.thecodecup.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecases.auth.LoginUseCase
import com.example.thecodecup.domain.usecases.auth.SendOtpUseCase
import com.example.thecodecup.domain.models.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class EmailVerificationRequired(val email: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sendOtpUseCase: SendOtpUseCase
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
                if (error is ApiException && error.code == EMAIL_NOT_VERIFIED) {
                    sendOtpUseCase(email)
                    _uiState.value = LoginUiState.EmailVerificationRequired(email.trim())
                } else {
                    _uiState.value = LoginUiState.Error(error.message ?: "Unknown error")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

private const val EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED"
