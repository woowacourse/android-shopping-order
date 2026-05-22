package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.product.Money
import java.time.LocalTime

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: String

    data class FixedDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Money,
        val minimumAmount: Money,
    ) : Coupon

    data class BuyXGetY(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShipping(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumAmount: Money,
    ) : Coupon

    data class PercentageDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discountPercentage: Int,
        val availableStartTime: LocalTime,
        val availableEndTime: LocalTime,
    ) : Coupon
}
