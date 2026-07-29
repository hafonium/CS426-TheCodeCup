package com.example.thecodecup.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.thecodecup.ui.App
import com.example.thecodecup.ui.auth.auth.AuthState
import com.example.thecodecup.ui.auth.auth.AuthViewModel
import com.example.thecodecup.ui.core.home.HomeScreen
import com.example.thecodecup.ui.core.home.HomeViewModel
import com.example.thecodecup.ui.auth.login.LoginScreen
import com.example.thecodecup.ui.auth.login.LoginViewModel
import com.example.thecodecup.ui.auth.register.RegisterScreen
import com.example.thecodecup.ui.auth.register.RegisterViewModel
import com.example.thecodecup.ui.auth.welcome.WelcomeScreen
import com.example.thecodecup.ui.auth.forgot.ChangeForgotPasswordScreen
import com.example.thecodecup.ui.auth.forgot.ForgotPasswordEmailScreen
import com.example.thecodecup.ui.auth.forgot.ForgotPasswordViewModel
import com.example.thecodecup.ui.auth.verification.OtpVerificationScreen
import com.example.thecodecup.ui.auth.verification.OtpVerificationViewModel
import com.example.thecodecup.ui.core.profile.ProfileScreen
import com.example.thecodecup.ui.core.profile.ProfileViewModel
import com.example.thecodecup.ui.core.splash.SplashScreen
import com.example.thecodecup.ui.core.details.DetailScreen
import com.example.thecodecup.ui.core.details.DetailViewModel
import com.example.thecodecup.ui.core.cart.CartScreen
import com.example.thecodecup.ui.core.cart.CartViewModel
import com.example.thecodecup.ui.core.order.OrderScreen
import com.example.thecodecup.ui.core.order.OrderSuccessScreen
import com.example.thecodecup.ui.core.order.OrderViewModel
import com.example.thecodecup.ui.core.rewards.RewardScreen
import com.example.thecodecup.ui.core.rewards.RewardViewModel
import com.example.thecodecup.ui.core.rewards.RedeemRewardScreen
import com.example.thecodecup.ui.core.rewards.RedeemRewardViewModel

@Composable
fun ScreenNavigator(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appInstance = context.applicationContext as App
    val sessionSnackbarHostState = remember { SnackbarHostState() }
    val cartViewModel: CartViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CartViewModel(
                    appInstance.getCartItemsUseCase,
                    appInstance.addCartItemUseCase,
                    appInstance.updateCartItemUseCase,
                    appInstance.updateCartQuantityUseCase,
                    appInstance.deleteCartItemUseCase,
                    appInstance.clearCartUseCase,
                    appInstance.getCurrentUserUseCase,
                    appInstance.createOrderUseCase
                )
            }
        }
    )

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

    LaunchedEffect(appInstance) {
        appInstance.sessionExpiredEvents.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            sessionSnackbarHostState.showSnackbar(
                message = "Your session has expired. Please log in again",
                duration = SnackbarDuration.Long
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        composable(route = Screen.Home.route) { backStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        HomeViewModel(
                            appInstance.getCurrentUserUseCase,
                            appInstance.getFoodsUseCase,
                            appInstance.getPromotionUseCase
                        )
                    }
                }
            )
            val refreshAfterProfileUpdate by backStackEntry.savedStateHandle
                .getStateFlow(PROFILE_UPDATED_KEY, false)
                .collectAsStateWithLifecycle()
            val refreshAfterOrderCompletion by backStackEntry.savedStateHandle
                .getStateFlow(ORDER_COMPLETED_KEY, false)
                .collectAsStateWithLifecycle()
            LaunchedEffect(refreshAfterProfileUpdate, refreshAfterOrderCompletion) {
                if (refreshAfterProfileUpdate || refreshAfterOrderCompletion) {
                    homeViewModel.refresh()
                    backStackEntry.savedStateHandle[PROFILE_UPDATED_KEY] = false
                    backStackEntry.savedStateHandle[ORDER_COMPLETED_KEY] = false
                }
            }
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToRewards = { navController.navigate(Screen.Rewards.route) },
                onNavigateToOrder = { navController.navigate(Screen.Order.route) },
                onNavigateToDetails = { navController.navigate(Screen.Details.createRoute(it)) }
            )
        }

        composable(route = Screen.Cart.route) { backStackEntry ->
            CartScreen(
                viewModel = cartViewModel,
                onNavigateBack = {
                    if (navController.currentBackStackEntry == backStackEntry) {
                        navController.popBackStack()
                    }
                },
                onNavigateToDetails = { navController.navigate(Screen.Details.createRoute(it)) },
                onOrderSuccess = {
                    navController.navigate(Screen.OrderSuccess.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Rewards.route) { backStackEntry ->
            val rewardViewModel: RewardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RewardViewModel(
                            appInstance.getPromotionUseCase,
                            appInstance.getGainedRewardsUseCase,
                            appInstance.getFoodsUseCase,
                            appInstance.getCurrentUserUseCase,
                            appInstance.useGachaponUseCase
                        )
                    }
                }
            )
            val refreshRewards by backStackEntry.savedStateHandle
                .getStateFlow(REWARDS_UPDATED_KEY, false)
                .collectAsStateWithLifecycle()
            LaunchedEffect(refreshRewards) {
                if (refreshRewards) {
                    rewardViewModel.refresh()
                    backStackEntry.savedStateHandle[REWARDS_UPDATED_KEY] = false
                }
            }
            RewardScreen(
                viewModel = rewardViewModel,
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onOrder = { navController.navigate(Screen.Order.route) },
                onRedeem = { navController.navigate(Screen.RedeemRewards.route) },
                onFood = { navController.navigate(Screen.Details.createRoute(it)) }
            )
        }

        composable(route = Screen.RedeemRewards.route) { backStackEntry ->
            val redeemViewModel: RedeemRewardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RedeemRewardViewModel(
                            appInstance.getRedeemRewardsUseCase,
                            appInstance.getPromotionUseCase,
                            appInstance.getCurrentUserUseCase,
                            appInstance.redeemRewardUseCase
                        )
                    }
                }
            )
            RedeemRewardScreen(
                viewModel = redeemViewModel,
                onBack = {
                    if (navController.currentBackStackEntry == backStackEntry) {
                        navController.popBackStack()
                    }
                },
                onFood = { navController.navigate(Screen.Details.createRoute(it)) },
                onRedeemed = {
                    runCatching { navController.getBackStackEntry(Screen.Rewards.route) }
                        .getOrNull()
                        ?.savedStateHandle
                        ?.set(REWARDS_UPDATED_KEY, true)
                }
            )
        }

        composable(route = Screen.Order.route) {
            val orderViewModel: OrderViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        OrderViewModel(
                            appInstance.getOrdersUseCase,
                            appInstance.completeOrderUseCase
                        )
                    }
                }
            )
            OrderScreen(
                viewModel = orderViewModel,
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onRewards = { navController.navigate(Screen.Rewards.route) },
                onFood = { navController.navigate(Screen.Details.createRoute(it)) },
                onOrderCompleted = {
                    runCatching { navController.getBackStackEntry(Screen.Home.route) }
                        .getOrNull()
                        ?.savedStateHandle
                        ?.set(ORDER_COMPLETED_KEY, true)
                    runCatching { navController.getBackStackEntry(Screen.Rewards.route) }
                        .getOrNull()
                        ?.savedStateHandle
                        ?.set(REWARDS_UPDATED_KEY, true)
                }
            )
        }

        composable(route = Screen.OrderSuccess.route) {
            OrderSuccessScreen(
                onTrackOrder = {
                    navController.navigate(Screen.Order.route) {
                        popUpTo(Screen.OrderSuccess.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Details.route) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")?.toIntOrNull() ?: return@composable
            val detailViewModel: DetailViewModel = viewModel(
                key = "details-$foodId",
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        DetailViewModel(foodId, appInstance.getFoodDetailsUseCase)
                    }
                }
            )
            DetailScreen(
                viewModel = detailViewModel,
                cartViewModel = cartViewModel,
                onNavigateBack = {
                    if (navController.currentBackStackEntry == backStackEntry) {
                        navController.popBackStack()
                    }
                },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(route = Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        RegisterViewModel(appInstance.registerUseCase, appInstance.sendOtpUseCase)
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
                },
                onNavigateToEmailVerification = {
                    navController.navigate(Screen.VerifyEmail.createRoute(it))
                }
            )
        }

        composable(
            route = Screen.VerifyEmail.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val verificationViewModel: OtpVerificationViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        OtpVerificationViewModel(
                            verifyOtp = appInstance.verifyEmailUseCase::invoke,
                            sendOtpUseCase = appInstance.sendOtpUseCase
                        )
                    }
                }
            )
            OtpVerificationScreen(
                email = email,
                title = "Verify your email",
                viewModel = verificationViewModel,
                onBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val appInstance = context.applicationContext as App
                        LoginViewModel(appInstance.loginUseCase, appInstance.sendOtpUseCase)
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
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.createRoute(it))
                },
                onNavigateToEmailVerification = {
                    navController.navigate(Screen.VerifyEmail.createRoute(it))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ForgotPassword.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val initialEmail = backStackEntry.arguments?.getString("email").orEmpty()
            val forgotViewModel: ForgotPasswordViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ForgotPasswordViewModel(sendOtpUseCase = appInstance.sendOtpUseCase)
                    }
                }
            )
            ForgotPasswordEmailScreen(
                initialEmail = initialEmail,
                viewModel = forgotViewModel,
                onBack = { navController.popBackStack() },
                onCodeSent = {
                    navController.navigate(Screen.VerifyForgotPassword.createRoute(it))
                }
            )
        }

        composable(
            route = Screen.VerifyForgotPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val verificationViewModel: OtpVerificationViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        OtpVerificationViewModel(
                            verifyOtp = appInstance.verifyForgotPasswordUseCase::invoke,
                            sendOtpUseCase = appInstance.sendOtpUseCase
                        )
                    }
                }
            )
            OtpVerificationScreen(
                email = email,
                title = "Verify reset code",
                viewModel = verificationViewModel,
                onBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Screen.ChangeForgotPassword.createRoute(email))
                }
            )
        }

        composable(
            route = Screen.ChangeForgotPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val forgotViewModel: ForgotPasswordViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ForgotPasswordViewModel(
                            changePasswordUseCase = appInstance.changeForgotPasswordUseCase
                        )
                    }
                }
            )
            ChangeForgotPasswordScreen(
                email = email,
                viewModel = forgotViewModel,
                onBack = { navController.popBackStack() },
                onPasswordChanged = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Profile.route) { backStackEntry ->
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
                    if (navController.currentBackStackEntry == backStackEntry) {
                        navController.popBackStack()
                    }
                },
                onProfileUpdated = {
                    runCatching { navController.getBackStackEntry(Screen.Home.route) }
                        .getOrNull()
                        ?.savedStateHandle
                        ?.set(PROFILE_UPDATED_KEY, true)
                }
            )
        }
        }
        SnackbarHost(
            hostState = sessionSnackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private const val PROFILE_UPDATED_KEY = "profile_updated"
private const val REWARDS_UPDATED_KEY = "rewards_updated"
private const val ORDER_COMPLETED_KEY = "order_completed"
