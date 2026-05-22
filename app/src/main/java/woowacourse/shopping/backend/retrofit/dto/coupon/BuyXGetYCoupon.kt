package woowacourse.shopping.backend.retrofit.dto.coupon

import kotlinx.serialization.Serializable

@Serializable
data class BuyXGetYCoupon(
    val id: Long?,
    val code: String,
    val description: String,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val discountType: String?
)
