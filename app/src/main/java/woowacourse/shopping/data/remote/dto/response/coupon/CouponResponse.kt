package woowacourse.shopping.data.remote.dto.response.coupon

import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int? = null,
    val minimumAmount: Long? = null,
    val availableTime: AvailableTimeResponse? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
)
