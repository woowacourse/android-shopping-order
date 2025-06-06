package woowacourse.shopping.data.coupon.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    val availableTime: AvailableTime?,
    val buyQuantity: Int?,
    val code: String,
    val description: String,
    val discount: Int,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int?,
    val id: Int,
    val minimumAmount: Int?,
)
