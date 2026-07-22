package com.example.thecodecup.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Profile : Screen("profile_screen")
    object Home : Screen("home_screen")
    object Cart : Screen("cart_screen")
    object Rewards : Screen("rewards_screen")
    object Order : Screen("order_screen")
    object Details : Screen("details_screen/{foodId}") {
        fun createRoute(foodId: Int) = "details_screen/$foodId"
    }
    object Splash : Screen("splash_screen")
}
