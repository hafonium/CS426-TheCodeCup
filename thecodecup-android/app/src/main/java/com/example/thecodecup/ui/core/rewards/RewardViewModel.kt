package com.example.thecodecup.ui.core.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.models.GainedRewardModel
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.usecases.auth.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecases.home.GetFoodsUseCase
import com.example.thecodecup.domain.usecases.rewards.GetGainedRewardsUseCase
import com.example.thecodecup.domain.usecases.rewards.GetPromotionUseCase
import com.example.thecodecup.domain.usecases.rewards.UseGachaponUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RewardUiState(
    val isLoading: Boolean = true,
    val loyaltyCount: Int = 0,
    val totalPoints: Int = 0,
    val gachaponCount: Int = 0,
    val history: List<GainedRewardModel> = emptyList(),
    val isLoadingMoreHistory: Boolean = false,
    val hasMoreHistory: Boolean = false,
    val foods: List<FoodModel> = emptyList(),
    val profileAddress: String = "",
    val gachaPrize: FoodModel? = null,
    val gachaSpinId: Int = 0,
    val isRequestingGacha: Boolean = false,
    val isSpinningGacha: Boolean = false,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val winMessage: String? = null
)

class RewardViewModel(
    private val getPromotionUseCase: GetPromotionUseCase,
    private val getGainedRewardsUseCase: GetGainedRewardsUseCase,
    private val getFoodsUseCase: GetFoodsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val useGachaponUseCase: UseGachaponUseCase
) : ViewModel() {
    private companion object {
        const val HISTORY_PAGE_SIZE = 8
        const val HISTORY_FETCH_SIZE = HISTORY_PAGE_SIZE + 1
    }

    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val promotionRequest = async { getPromotionUseCase() }
            val historyRequest = async {
                getGainedRewardsUseCase(limit = HISTORY_FETCH_SIZE, offset = 0)
            }
            val foodsRequest = async { getFoodsUseCase() }
            val userRequest = async { getCurrentUserUseCase() }
            val promotion = promotionRequest.await()
            val history = historyRequest.await()
            val foods = foodsRequest.await()
            val user = userRequest.await()
            _uiState.value = RewardUiState(
                isLoading = false,
                loyaltyCount = promotion.getOrNull()?.loyaltyCount ?: 0,
                totalPoints = promotion.getOrNull()?.totalRewardPoint ?: 0,
                gachaponCount = promotion.getOrNull()?.gachaponCount ?: 0,
                history = history.getOrDefault(emptyList()).take(HISTORY_PAGE_SIZE),
                hasMoreHistory = history.getOrDefault(emptyList()).size > HISTORY_PAGE_SIZE,
                foods = foods.getOrDefault(emptyList()),
                profileAddress = user.getOrNull()?.address.orEmpty(),
                errorMessage = promotion.exceptionOrNull()?.message
                    ?: history.exceptionOrNull()?.message
            )
        }
    }

    fun loadMoreHistory() {
        val state = _uiState.value
        if (state.isLoadingMoreHistory || !state.hasMoreHistory) return

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoadingMoreHistory = true,
                feedbackMessage = null
            )
            getGainedRewardsUseCase(
                limit = HISTORY_FETCH_SIZE,
                offset = state.history.size
            ).fold(
                onSuccess = { newRewards ->
                    _uiState.value = _uiState.value.copy(
                        history = (_uiState.value.history + newRewards.take(HISTORY_PAGE_SIZE))
                            .distinctBy { it.id },
                        isLoadingMoreHistory = false,
                        hasMoreHistory = newRewards.size > HISTORY_PAGE_SIZE
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoadingMoreHistory = false,
                        feedbackMessage = it.message ?: "Unable to load more reward history"
                    )
                }
            )
        }
    }

    fun useGachapon(address: String) {
        if (_uiState.value.isRequestingGacha || _uiState.value.isSpinningGacha) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRequestingGacha = true,
                feedbackMessage = null
            )
            useGachaponUseCase(address).fold(
                onSuccess = { result ->
                    _uiState.value = _uiState.value.copy(
                        loyaltyCount = result.promotion.loyaltyCount,
                        totalPoints = result.promotion.totalRewardPoint,
                        gachaponCount = result.promotion.gachaponCount,
                        gachaPrize = result.food,
                        gachaSpinId = _uiState.value.gachaSpinId + 1,
                        isRequestingGacha = false,
                        isSpinningGacha = true
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isRequestingGacha = false,
                        feedbackMessage = it.message ?: "Unable to use gachapon"
                    )
                }
            )
        }
    }

    fun finishGachaSpin() {
        val prize = _uiState.value.gachaPrize ?: return
        _uiState.value = _uiState.value.copy(
            isSpinningGacha = false,
            winMessage = "You won ${prize.name}! View it in your order."
        )
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    fun clearWinMessage() {
        _uiState.value = _uiState.value.copy(winMessage = null)
    }
}
