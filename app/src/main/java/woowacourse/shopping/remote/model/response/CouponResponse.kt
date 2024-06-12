package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    val id: Int,
    val code: CouponCodeResponse,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeResponse? = null,
    val discountType: DiscountTypeResponse,
)
