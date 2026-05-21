package woowacourse.shopping.model

import java.time.LocalDate

sealed interface Coupon {
    val id: Long?
    val code: String
    val description: String
    val expirationDate: LocalDate

    data class FixedDiscount(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Money,
        val minimumAmount: Money,
    ) : Coupon

    data class BuyXGetY(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShipping(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Money,
    ) : Coupon

    data class PercentageDiscount(
        override val id: Long?,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercent: Int,
        val availableTime: AvailableTime,
    ) : Coupon
}
