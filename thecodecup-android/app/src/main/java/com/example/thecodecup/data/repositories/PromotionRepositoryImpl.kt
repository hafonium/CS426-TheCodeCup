package com.example.thecodecup.data.repositories

import com.example.thecodecup.data.local.prefs.AuthPreferences
import com.example.thecodecup.data.remote.dto.FoodResponseDto
import com.example.thecodecup.data.remote.dto.GachaUseDto
import com.example.thecodecup.data.remote.dto.RedeemRewardUseDto
import com.example.thecodecup.data.remote.network.ApiClient
import com.example.thecodecup.domain.models.FoodModel
import com.example.thecodecup.domain.models.FoodOptionModel
import com.example.thecodecup.domain.models.FoodOptionTypeModel
import com.example.thecodecup.domain.models.GainedRewardModel
import com.example.thecodecup.domain.models.GachaResultModel
import com.example.thecodecup.domain.models.PromotionModel
import com.example.thecodecup.domain.models.RedeemRewardModel
import com.example.thecodecup.domain.repositories.PromotionRepository
import com.example.thecodecup.utils.getHttpMessage
import retrofit2.HttpException

class PromotionRepositoryImpl(
    private val authPreferences: AuthPreferences
) : PromotionRepository {
    private val promotionApi = ApiClient.promotionApiService
    private val redeemApi = ApiClient.redeemRewardApiService

    override suspend fun getPromotion() = call {
        promotionApi.getPromotions(token()).let {
            PromotionModel(it.totalRewardPoint, it.loyaltyCount, it.gachaponCount)
        }
    }

    override suspend fun getGainedRewards(limit: Int, offset: Int) = call {
        promotionApi.getGainedRewards(
            token = token(),
            limit = limit,
            offset = offset
        ).map {
            GainedRewardModel(it.id, it.food.toDomain(), it.gainedPoint, it.createdAt)
        }
    }

    override suspend fun getRedeemRewards(limit: Int, offset: Int) = call {
        redeemApi.getRedeemRewards(limit = limit, offset = offset).map {
            RedeemRewardModel(it.id, it.food.toDomain(), it.expirationTime, it.requiredPoint)
        }
    }

    override suspend fun redeemReward(rewardId: Int, address: String) = call {
        promotionApi.useRewardPoint(token(), RedeemRewardUseDto(address, rewardId)).let {
            PromotionModel(it.totalRewardPoint, it.loyaltyCount, it.gachaponCount)
        }
    }

    override suspend fun useGachapon(address: String) = call {
        promotionApi.useGachapon(token(), GachaUseDto(address)).let {
            GachaResultModel(
                promotion = PromotionModel(
                    it.promotion.totalRewardPoint,
                    it.promotion.loyaltyCount,
                    it.promotion.gachaponCount
                ),
                food = it.food.toDomain()
            )
        }
    }

    private fun token() =
        "Bearer ${authPreferences.getAuthToken() ?: throw IllegalStateException("Please sign in again")}"

    private suspend fun <T> call(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: HttpException) {
        Result.failure(Exception(getHttpMessage(e)))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Unable to load rewards"))
    }

    private fun FoodResponseDto.toDomain() = FoodModel(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        category = category,
        rewardPoint = rewardPoint,
        options = options.map { option ->
            FoodOptionModel(
                id = option.id,
                name = option.name,
                types = option.foodOptionTypes.map { type ->
                    FoodOptionTypeModel(type.id, type.name, type.price)
                }
            )
        }
    )
}
