package com.example.thecodecup.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.data.local.AppDatabase
import com.example.thecodecup.data.local.AppDatabase.Companion.getDatabase
import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.repository.UserRepositoryImpl
import com.example.thecodecup.domain.usecase.account.LoginUseCase
import com.example.thecodecup.domain.usecase.account.RegisterUseCase
import com.example.thecodecup.ui.core.home.HomeScreen
import com.example.thecodecup.ui.auth.login.LoginScreen
import com.example.thecodecup.ui.auth.login.LoginViewModel
import com.example.thecodecup.ui.core.profile.ProfileScreen
import com.example.thecodecup.ui.auth.register.RegisterScreen
import com.example.thecodecup.ui.auth.register.RegisterViewModel
import com.example.thecodecup.ui.auth.welcome.WelcomeScreen

@Composable
fun ScreenNavigator(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authPrefs = AuthPreferences(context)
    val isUserLoggedIn = authPrefs.isLoggedIn()
    val startScreen = if (isUserLoggedIn) Screen.Home.route else Screen.Welcome.route

    NavHost(navController = navController, startDestination = startScreen) {

        val onNavigateToWelcome = {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }

        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(route = Screen.Login.route) {
            val context = LocalContext.current
            val loginViewModelFactory = viewModelFactory {
                initializer {
                    val userDao = getDatabase(context).userDao()
                    val repository = UserRepositoryImpl(userDao)
                    val authPrefs = AuthPreferences(context)
                    val useCase = LoginUseCase(repository, authPrefs)

                    LoginViewModel(useCase)
                }
            }

            val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)

            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToWelcome = onNavigateToWelcome,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(route = Screen.Register.route) {
            val context = LocalContext.current
            val registerViewModelFactory = viewModelFactory {
                initializer {
                    val userDao = getDatabase(context).userDao()
                    val repository = UserRepositoryImpl(userDao)
                    val useCase = RegisterUseCase(repository)

                    RegisterViewModel(useCase)
                }
            }

            val registerViewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToWelcome = onNavigateToWelcome,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.Login.route) },
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
            )
        }
    }
}
