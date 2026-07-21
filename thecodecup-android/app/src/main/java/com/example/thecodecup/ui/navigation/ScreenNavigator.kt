package com.example.thecodecup.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.ui.App
import com.example.thecodecup.ui.auth.auth.AuthState
import com.example.thecodecup.ui.auth.auth.AuthViewModel
import com.example.thecodecup.ui.core.home.HomeScreen
import com.example.thecodecup.ui.auth.login.LoginScreen
import com.example.thecodecup.ui.auth.login.LoginViewModel
import com.example.thecodecup.ui.auth.register.RegisterScreen
import com.example.thecodecup.ui.auth.register.RegisterViewModel
import com.example.thecodecup.ui.auth.welcome.WelcomeScreen
import com.example.thecodecup.ui.core.profile.ProfileScreen
import com.example.thecodecup.ui.core.profile.ProfileViewModel
import com.example.thecodecup.ui.core.splash.SplashScreen

@Composable
fun ScreenNavigator(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.Guest -> {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.Checking -> {
                // Still checking, do nothing for now
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen()
        }

        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(route = Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        RegisterViewModel(appInstance.registerUseCase)
                    }
                }
            )

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        LoginViewModel(appInstance.loginUseCase)
                    }
                }
            )

            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        ProfileViewModel(
                            appInstance.logoutUseCase,
                            appInstance.getCurrentUserUseCase,
                            appInstance.updateUserUseCase
                        )
                    }
                }

            )

            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }
    }
}