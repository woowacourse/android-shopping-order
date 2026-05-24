package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate

    data class FixedCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountAmount: Int,
        val minimumAmount: Int,
    ) : Coupon

    data class PercentageCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercentage: Int,
        val availableTime: TimeRange,
    ) : Coupon

    data class BuyXGetYCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon
}

data class TimeRange(
    val start: LocalTime,
    val end: LocalTime,
)
