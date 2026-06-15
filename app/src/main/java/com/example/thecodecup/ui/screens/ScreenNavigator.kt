package com.example.thecodecup.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.ui.screens.login.LoginScreen
import com.example.thecodecup.ui.screens.welcome.WelcomeScreen
import com.example.thecodecup.utils.ScreenWrapper

@Composable
fun ScreenNavigator(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {

        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToWelcome = { navController.popBackStack() }
            )
        }

    }
}
