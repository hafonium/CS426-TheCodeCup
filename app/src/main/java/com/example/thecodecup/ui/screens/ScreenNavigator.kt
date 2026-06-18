package com.example.thecodecup.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.ui.screens.home.HomeScreen
import com.example.thecodecup.ui.screens.login.LoginScreen
import com.example.thecodecup.ui.screens.profile.ProfileScreen
import com.example.thecodecup.ui.screens.register.RegisterScreen
import com.example.thecodecup.ui.screens.welcome.WelcomeScreen

@Composable
fun ScreenNavigator(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {

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
            LoginScreen(
                onNavigateToWelcome = onNavigateToWelcome,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
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
