package com.example.thecodecup.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object VerifyEmail : Screen("verify_email/{email}") {
        fun createRoute(email: String) = "verify_email/${android.net.Uri.encode(email)}"
    }
    object ForgotPassword : Screen("forgot_password?email={email}") {
        fun createRoute(email: String) = "forgot_password?email=${android.net.Uri.encode(email)}"
    }
    object VerifyForgotPassword : Screen("verify_forgot_password/{email}") {
        fun createRoute(email: String) = "verify_forgot_password/${android.net.Uri.encode(email)}"
    }
    object ChangeForgotPassword : Screen("change_forgot_password/{email}") {
        fun createRoute(email: String) = "change_forgot_password/${android.net.Uri.encode(email)}"
    }
    object Profile : Screen("profile_screen")
    object Home : Screen("home_screen")
    object Cart : Screen("cart_screen")
    object Rewards : Screen("rewards_screen")
    object RedeemRewards : Screen("redeem_rewards_screen")
    object Order : Screen("order_screen")
    object OrderSuccess : Screen("order_success_screen")
    object Details : Screen("details_screen/{foodId}") {
        fun createRoute(foodId: Int) = "details_screen/$foodId"
    }
    object Splash : Screen("splash_screen")
}
