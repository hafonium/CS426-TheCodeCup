package com.example.thecodecup.ui.core.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.UserResponseModel
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.auth.LogoutUseCase
import com.example.thecodecup.domain.usecases.profile.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class LoggedIn(
        val user: UserResponseModel,
        val passwordUpdateVersion: Long = 0,
        val profileUpdateVersion: Long = 0
    ) : ProfileUiState()
    object LoggedOut : ProfileUiState()
    object Updated : ProfileUiState()
    data class Error(
        val message: String,
        val user: UserResponseModel?,
        val passwordUpdateVersion: Long = 0,
        val profileUpdateVersion: Long = 0
    ) : ProfileUiState()
}

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
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
                _uiState.value = ProfileUiState.Error(error.message ?: "Unknown error", null)
            }
        }
    }

    fun fetchCurrentUser() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            result.onSuccess { user ->
                _uiState.value = ProfileUiState.LoggedIn(user)
            }.onFailure { error ->
                _uiState.value = ProfileUiState.Error(error.message ?: "Unknown error", null)
            }
        }
    }

    fun updateUser(
        email: String,
        fullName: String,
        phone: String,
        avatarUrl: String?,
        address: String,
        oldPassword: String?,
        newPassword: String?,
        confirmNewPassword: String?
    ) {
        // 1. Grab the current user before triggering loading
        val currentUser = when (val state = _uiState.value) {
            is ProfileUiState.LoggedIn -> state.user
            is ProfileUiState.Error -> state.user // Assuming Error also holds a user property
            else -> null
        }
        val passwordUpdateVersion = when (val state = _uiState.value) {
            is ProfileUiState.LoggedIn -> state.passwordUpdateVersion
            is ProfileUiState.Error -> state.passwordUpdateVersion
            else -> 0
        }
        val profileUpdateVersion = when (val state = _uiState.value) {
            is ProfileUiState.LoggedIn -> state.profileUpdateVersion
            is ProfileUiState.Error -> state.profileUpdateVersion
            else -> 0
        }
        val profileInformationChanged = currentUser != null && (
            currentUser.email != email ||
                currentUser.fullName != fullName ||
                currentUser.phoneNumber != phone ||
                currentUser.avatarUrl != avatarUrl ||
                currentUser.address != address
            )

//        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            val result = updateUserUseCase(email, fullName, phone, avatarUrl, address,
                oldPassword, newPassword, confirmNewPassword)

            result.onSuccess {
                // 2. On success: Copy the existing user with the newly updated fields locally
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        email = email,
                        fullName = fullName,
                        phoneNumber = phone,
                        avatarUrl = avatarUrl,
                        address = address
                    )
                    _uiState.value = ProfileUiState.LoggedIn(
                        user = updatedUser,
                        passwordUpdateVersion = if (oldPassword != null) {
                            passwordUpdateVersion + 1
                        } else {
                            passwordUpdateVersion
                        },
                        profileUpdateVersion = profileUpdateVersion + if (profileInformationChanged) 1 else 0
                    )
                } else {
                    fetchCurrentUser() // Fallback only if currentUser was somehow null
                }
            }.onFailure { error ->
                // 3. On failure: Restore the original currentUser back to Error state
                _uiState.value = ProfileUiState.Error(
                    message = error.message ?: "Unknown error",
                    user = currentUser,
                    passwordUpdateVersion = passwordUpdateVersion,
                    profileUpdateVersion = profileUpdateVersion
                )
            }
        }
    }
    fun resetState() {
        _uiState.value = ProfileUiState.Idle
    }
}
