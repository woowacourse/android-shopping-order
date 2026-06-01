package woowacourse.shopping.data.remote.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class CouponItem(
    val availableTime: AvailableTime? = null,
    val buyQuantity: Int = 0,
    val code: String,
    val description: String,
    val discount: Int = 0,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int = 0,
    val id: Long,
    val minimumAmount: Int = 0,
)
