package com.example.thecodecup.ui.auth.auth

import com.example.thecodecup.domain.models.UserResponseModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    object Checking : AuthState
    data class Authenticated(val user: UserResponseModel) : AuthState
    object Guest : AuthState
}

class AuthViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthentication()
    }

    fun checkAuthentication() {
        viewModelScope.launch {
            _authState.value = AuthState.Checking
            val result = getCurrentUserUseCase()

            result.onSuccess { userModel ->
                _authState.value = AuthState.Authenticated(userModel)
            }.onFailure {
                _authState.value = AuthState.Guest
            }
        }
    }
}