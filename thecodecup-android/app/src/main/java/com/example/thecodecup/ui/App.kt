package com.example.thecodecup.ui

import android.app.Application
import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.repositories.AuthRepositoryImpl
import com.example.thecodecup.data.repositories.UserRepositoryImpl
import com.example.thecodecup.data.repositories.FoodRepositoryImpl
import com.example.thecodecup.data.repositories.CartRepositoryImpl
import com.example.thecodecup.domain.repositories.AuthRepository
import com.example.thecodecup.domain.repositories.UserRepository
import com.example.thecodecup.domain.repositories.FoodRepository
import com.example.thecodecup.domain.repositories.CartRepository
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.auth.LoginUseCase
import com.example.thecodecup.domain.usecases.auth.LogoutUseCase
import com.example.thecodecup.domain.usecases.auth.RegisterUseCase

class App(): Application() {
    private val authPrefs by lazy { AuthPreferences(applicationContext) }

    // Repositories
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authPrefs) }
    val userRepository: UserRepository by lazy { UserRepositoryImpl(authPrefs) }
    val foodRepository: FoodRepository by lazy { FoodRepositoryImpl() }
    val cartRepository: CartRepository by lazy { CartRepositoryImpl(authPrefs) }

    // Use Cases
    val loginUseCase by lazy { LoginUseCase(authRepository, authPrefs) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository, authPrefs) }
    val registerUseCase by lazy { RegisterUseCase(userRepository) }
    val getCurrentUserUseCase by lazy { GetCurrentUserUseCase(userRepository) }
    val getFoodsUseCase by lazy { com.example.thecodecup.domain.usecases.home.GetFoodsUseCase(foodRepository) }
    val getFoodDetailsUseCase by lazy { com.example.thecodecup.domain.usecases.details.GetFoodDetailsUseCase(foodRepository) }
    val getCartItemsUseCase by lazy { com.example.thecodecup.domain.usecases.cart.GetCartItemsUseCase(cartRepository) }
    val addCartItemUseCase by lazy { com.example.thecodecup.domain.usecases.cart.AddCartItemUseCase(cartRepository) }
    val updateCartItemUseCase by lazy { com.example.thecodecup.domain.usecases.cart.UpdateCartItemUseCase(cartRepository) }
    val updateCartQuantityUseCase by lazy { com.example.thecodecup.domain.usecases.cart.UpdateCartQuantityUseCase(cartRepository) }
    val deleteCartItemUseCase by lazy { com.example.thecodecup.domain.usecases.cart.DeleteCartItemUseCase(cartRepository) }
    val clearCartUseCase by lazy { com.example.thecodecup.domain.usecases.cart.ClearCartUseCase(cartRepository) }
    val updateUserUseCase by lazy { com.example.thecodecup.domain.usecases.profile.UpdateUserUseCase(userRepository) }
}
