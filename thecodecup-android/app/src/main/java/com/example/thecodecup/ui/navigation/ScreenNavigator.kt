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
import com.example.thecodecup.ui.core.home.HomeViewModel
import com.example.thecodecup.ui.core.home.HomeDestinationPlaceholder
import com.example.thecodecup.ui.auth.login.LoginScreen
import com.example.thecodecup.ui.auth.login.LoginViewModel
import com.example.thecodecup.ui.auth.register.RegisterScreen
import com.example.thecodecup.ui.auth.register.RegisterViewModel
import com.example.thecodecup.ui.auth.welcome.WelcomeScreen
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

@Composable
fun ScreenNavigator(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appInstance = context.applicationContext as App
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
                        HomeViewModel(appInstance.getCurrentUserUseCase, appInstance.getFoodsUseCase)
                    }
                }
            )
            val refreshAfterProfileUpdate by backStackEntry.savedStateHandle
                .getStateFlow(PROFILE_UPDATED_KEY, false)
                .collectAsStateWithLifecycle()
            LaunchedEffect(refreshAfterProfileUpdate) {
                if (refreshAfterProfileUpdate) {
                    homeViewModel.refresh()
                    backStackEntry.savedStateHandle[PROFILE_UPDATED_KEY] = false
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
            HomeDestinationPlaceholder(
                "Rewards",
                onNavigateBack = {
                    if (navController.currentBackStackEntry == backStackEntry) {
                        navController.popBackStack()
                    }
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
                onFood = { navController.navigate(Screen.Details.createRoute(it)) }
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
}

private const val PROFILE_UPDATED_KEY = "profile_updated"
