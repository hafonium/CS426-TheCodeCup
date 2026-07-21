package com.example.thecodecup.ui

import android.app.Application
import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.repository.AuthRepositoryImpl
import com.example.thecodecup.data.repository.UserRepositoryImpl
import com.example.thecodecup.domain.repository.AuthRepository
import com.example.thecodecup.domain.repository.UserRepository
import com.example.thecodecup.domain.usecase.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecase.auth.LoginUseCase
import com.example.thecodecup.domain.usecase.auth.LogoutUseCase
import com.example.thecodecup.domain.usecase.auth.RegisterUseCase

class App(): Application() {
    private val authPrefs by lazy { AuthPreferences(applicationContext) }

    // Repositories
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authPrefs) }
    val userRepository: UserRepository by lazy { UserRepositoryImpl(authPrefs) }

    // Use Cases
    val loginUseCase by lazy { LoginUseCase(authRepository, authPrefs) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository, authPrefs) }
    val registerUseCase by lazy { RegisterUseCase(userRepository) }
    val getCurrentUserUseCase by lazy { GetCurrentUserUseCase(userRepository) }
    val updateUserUseCase by lazy { com.example.thecodecup.domain.usecase.profile.UpdateUserUseCase(userRepository) }
}