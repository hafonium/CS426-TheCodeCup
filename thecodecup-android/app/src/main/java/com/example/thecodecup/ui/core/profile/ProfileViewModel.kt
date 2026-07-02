package com.example.thecodecup.ui.core.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecase.auth.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    object LoggedIn : ProfileUiState() // change to data class LoggedIn(val user: UserResponseModel) : ProfileState()
    object LoggedOut : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onLogoutClicked() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            val result = logoutUseCase()
            result.onSuccess {
                _uiState.value = ProfileUiState.LoggedOut
            }.onFailure { error ->
                _uiState.value = ProfileUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = ProfileUiState.Idle
    }
}