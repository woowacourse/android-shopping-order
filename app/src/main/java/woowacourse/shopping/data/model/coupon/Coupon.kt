package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable

@Serializable
sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
    abstract val discountType: String
}
