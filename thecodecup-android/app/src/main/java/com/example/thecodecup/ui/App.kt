package com.example.thecodecup.ui

import android.app.Application
import com.example.thecodecup.BuildConfig
import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.repositories.AuthRepositoryImpl
import com.example.thecodecup.data.repositories.UserRepositoryImpl
import com.example.thecodecup.data.repositories.FoodRepositoryImpl
import com.example.thecodecup.data.repositories.CartRepositoryImpl
import com.example.thecodecup.data.repositories.OrderRepositoryImpl
import com.example.thecodecup.data.repositories.PromotionRepositoryImpl
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.repositories.AuthRepository
import com.example.thecodecup.domain.repositories.UserRepository
import com.example.thecodecup.domain.repositories.FoodRepository
import com.example.thecodecup.domain.repositories.CartRepository
import com.example.thecodecup.domain.repositories.OrderRepository
import com.example.thecodecup.domain.repositories.PromotionRepository
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.auth.LoginUseCase
import com.example.thecodecup.domain.usecases.auth.LogoutUseCase
import com.example.thecodecup.domain.usecases.auth.RegisterUseCase
import com.example.thecodecup.domain.usecases.auth.ChangeForgotPasswordUseCase
import com.example.thecodecup.domain.usecases.auth.SendOtpUseCase
import com.example.thecodecup.domain.usecases.auth.VerifyEmailUseCase
import com.example.thecodecup.domain.usecases.auth.VerifyForgotPasswordUseCase
import org.osmdroid.config.Configuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class App(): Application() {
    private val _sessionExpiredEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvents = _sessionExpiredEvents.asSharedFlow()

    override fun onCreate() {
        super.onCreate()
        ApiClient.setUnauthorizedHandler {
            synchronized(authPrefs) {
                if (authPrefs.getAuthToken() != null) {
                    authPrefs.clearAuthToken()
                    _sessionExpiredEvents.tryEmit(Unit)
                }
            }
        }
        Configuration.getInstance().apply {
            load(applicationContext, getSharedPreferences("openstreetmap", MODE_PRIVATE))
            userAgentValue =
                "TheCodeCupAndroid/${BuildConfig.VERSION_NAME} (${BuildConfig.APPLICATION_ID})"
        }
    }

    private val authPrefs by lazy { AuthPreferences(applicationContext) }

    // Repositories
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authPrefs) }
    val userRepository: UserRepository by lazy { UserRepositoryImpl(authPrefs) }
    val foodRepository: FoodRepository by lazy { FoodRepositoryImpl() }
    val cartRepository: CartRepository by lazy { CartRepositoryImpl(authPrefs) }
    val orderRepository: OrderRepository by lazy { OrderRepositoryImpl(authPrefs) }
    val promotionRepository: PromotionRepository by lazy { PromotionRepositoryImpl(authPrefs) }

    // Use Cases
    val loginUseCase by lazy { LoginUseCase(authRepository, authPrefs) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository, authPrefs) }
    val registerUseCase by lazy { RegisterUseCase(userRepository) }
    val sendOtpUseCase by lazy { SendOtpUseCase(userRepository) }
    val verifyEmailUseCase by lazy { VerifyEmailUseCase(userRepository) }
    val verifyForgotPasswordUseCase by lazy { VerifyForgotPasswordUseCase(userRepository) }
    val changeForgotPasswordUseCase by lazy { ChangeForgotPasswordUseCase(userRepository) }
    val getCurrentUserUseCase by lazy { GetCurrentUserUseCase(userRepository) }
    val getFoodsUseCase by lazy { com.example.thecodecup.domain.usecases.home.GetFoodsUseCase(foodRepository) }
    val getFoodDetailsUseCase by lazy { com.example.thecodecup.domain.usecases.details.GetFoodDetailsUseCase(foodRepository) }
    val getCartItemsUseCase by lazy { com.example.thecodecup.domain.usecases.cart.GetCartItemsUseCase(cartRepository) }
    val addCartItemUseCase by lazy { com.example.thecodecup.domain.usecases.cart.AddCartItemUseCase(cartRepository) }
    val updateCartItemUseCase by lazy { com.example.thecodecup.domain.usecases.cart.UpdateCartItemUseCase(cartRepository) }
    val updateCartQuantityUseCase by lazy { com.example.thecodecup.domain.usecases.cart.UpdateCartQuantityUseCase(cartRepository) }
    val deleteCartItemUseCase by lazy { com.example.thecodecup.domain.usecases.cart.DeleteCartItemUseCase(cartRepository) }
    val clearCartUseCase by lazy { com.example.thecodecup.domain.usecases.cart.ClearCartUseCase(cartRepository) }
    val createOrderUseCase by lazy { com.example.thecodecup.domain.usecases.order.CreateOrderUseCase(orderRepository) }
    val getOrdersUseCase by lazy { com.example.thecodecup.domain.usecases.order.GetOrdersUseCase(orderRepository) }
    val completeOrderUseCase by lazy { com.example.thecodecup.domain.usecases.order.CompleteOrderUseCase(orderRepository) }
    val updateUserUseCase by lazy { com.example.thecodecup.domain.usecases.profile.UpdateUserUseCase(userRepository) }
    val getPromotionUseCase by lazy { com.example.thecodecup.domain.usecases.rewards.GetPromotionUseCase(promotionRepository) }
    val getGainedRewardsUseCase by lazy { com.example.thecodecup.domain.usecases.rewards.GetGainedRewardsUseCase(promotionRepository) }
    val getRedeemRewardsUseCase by lazy { com.example.thecodecup.domain.usecases.rewards.GetRedeemRewardsUseCase(promotionRepository) }
    val redeemRewardUseCase by lazy { com.example.thecodecup.domain.usecases.rewards.RedeemRewardUseCase(promotionRepository) }
    val useGachaponUseCase by lazy { com.example.thecodecup.domain.usecases.rewards.UseGachaponUseCase(promotionRepository) }
}
