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
    val isPageLoading: Boolean = false,
    val rewards: List<RedeemRewardModel> = emptyList(),
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
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
    private companion object {
        const val PAGE_SIZE = 8
        const val PAGE_FETCH_SIZE = PAGE_SIZE + 1
    }

    private val _uiState = MutableStateFlow(RedeemRewardUiState())
    val uiState: StateFlow<RedeemRewardUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val rewardsRequest = async {
                getRedeemRewardsUseCase(limit = PAGE_FETCH_SIZE, offset = 0)
            }
            val promotionRequest = async { getPromotionUseCase() }
            val userRequest = async { getCurrentUserUseCase() }
            val rewards = rewardsRequest.await()
            val promotion = promotionRequest.await()
            val user = userRequest.await()
            _uiState.value = RedeemRewardUiState(
                isLoading = false,
                rewards = rewards.getOrDefault(emptyList()).take(PAGE_SIZE),
                currentPage = 1,
                hasNextPage = rewards.getOrDefault(emptyList()).size > PAGE_SIZE,
                totalPoints = promotion.getOrNull()?.totalRewardPoint ?: 0,
                profileAddress = user.getOrNull()?.address.orEmpty(),
                errorMessage = rewards.exceptionOrNull()?.message
                    ?: promotion.exceptionOrNull()?.message
                    ?: user.exceptionOrNull()?.message
            )
        }
    }

    fun previousPage() {
        val targetPage = _uiState.value.currentPage - 1
        if (targetPage < 1) return
        loadPage(targetPage)
    }

    fun nextPage() {
        val state = _uiState.value
        if (!state.hasNextPage) return
        loadPage(state.currentPage + 1)
    }

    private fun loadPage(page: Int) {
        if (_uiState.value.isPageLoading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPageLoading = true,
                errorMessage = null
            )
            getRedeemRewardsUseCase(
                limit = PAGE_FETCH_SIZE,
                offset = (page - 1) * PAGE_SIZE
            ).fold(
                onSuccess = { rewards ->
                    _uiState.value = _uiState.value.copy(
                        isPageLoading = false,
                        rewards = rewards.take(PAGE_SIZE),
                        currentPage = page,
                        hasNextPage = rewards.size > PAGE_SIZE
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isPageLoading = false,
                        errorMessage = it.message ?: "Unable to load this page"
                    )
                }
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
                        message = "${reward.food.name} redeemed successfully. View it in your order."
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
