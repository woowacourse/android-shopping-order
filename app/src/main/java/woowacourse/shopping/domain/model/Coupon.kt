package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: LocalDate

    data class FixedAmountCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon()

    data class BuyXGetYCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon()

    data class FreeShippingCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon()

    data class PercentageCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercent: Int,
        val availableTime: TimeRange,
    ) : Coupon()
}

data class TimeRange(
    val start: LocalTime,
    val end: LocalTime,
)
