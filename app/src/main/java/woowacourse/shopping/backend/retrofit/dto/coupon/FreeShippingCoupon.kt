package woowacourse.shopping.backend.retrofit.dto.coupon

import kotlinx.serialization.Serializable

@Serializable
data class FreeShippingCoupon(
    val id: Long?,
    val code: String,
    val description: String,
    val expirationDate: String,
    val minimumAmount: Int?,
    val discountType: String?
)
