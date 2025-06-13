package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable

@Serializable
sealed interface CouponResponse {
    val id: Long
    val code: String
    val description: String
    val expirationDate: String
    val discountType: String
}
