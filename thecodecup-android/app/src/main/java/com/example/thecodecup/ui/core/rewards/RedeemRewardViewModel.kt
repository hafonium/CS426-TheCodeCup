package com.example.thecodecup.ui.core.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.RedeemRewardModel
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.rewards.GetPromotionUseCase
import com.example.thecodecup.domain.usecases.rewards.GetRedeemRewardsUseCase
import com.example.thecodecup.domain.usecases.rewards.RedeemRewardUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RedeemRewardUiState(
    val isLoading: Boolean = true,
    val rewards: List<RedeemRewardModel> = emptyList(),
    val totalPoints: Int = 0,
    val profileAddress: String = "",
    val redeemingRewardId: Int? = null,
    val errorMessage: String? = null,
    val message: String? = null
)

class RedeemRewardViewModel(
    private val getRedeemRewardsUseCase: GetRedeemRewardsUseCase,
    private val getPromotionUseCase: GetPromotionUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val redeemRewardUseCase: RedeemRewardUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RedeemRewardUiState())
    val uiState: StateFlow<RedeemRewardUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val rewardsRequest = async { getRedeemRewardsUseCase() }
            val promotionRequest = async { getPromotionUseCase() }
            val userRequest = async { getCurrentUserUseCase() }
            val rewards = rewardsRequest.await()
            val promotion = promotionRequest.await()
            val user = userRequest.await()
            _uiState.value = RedeemRewardUiState(
                isLoading = false,
                rewards = rewards.getOrDefault(emptyList()),
                totalPoints = promotion.getOrNull()?.totalRewardPoint ?: 0,
                profileAddress = user.getOrNull()?.address.orEmpty(),
                errorMessage = rewards.exceptionOrNull()?.message
                    ?: promotion.exceptionOrNull()?.message
                    ?: user.exceptionOrNull()?.message
            )
        }
    }

    fun redeem(reward: RedeemRewardModel, address: String) {
        if (_uiState.value.redeemingRewardId != null) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                redeemingRewardId = reward.id,
                errorMessage = null,
                message = null
            )
            redeemRewardUseCase(reward.id, address).fold(
                onSuccess = { promotion ->
                    _uiState.value = _uiState.value.copy(
                        redeemingRewardId = null,
                        totalPoints = promotion.totalRewardPoint,
                        message = "${reward.food.name} redeemed successfully"
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        redeemingRewardId = null,
                        errorMessage = it.message ?: "Unable to redeem this reward"
                    )
                }
            )
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(errorMessage = null, message = null)
    }
}
