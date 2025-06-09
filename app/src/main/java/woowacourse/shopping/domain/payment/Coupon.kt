package woowacourse.shopping.domain.payment

import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate

    data class FixedDiscountCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon

    data class BuyNGetNCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShippingCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon

    data class PercentageCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountRate: Double,
        val availableTime: AvailableTime,
    ) : Coupon {
        data class AvailableTime(
            val start: LocalTime,
            val end: LocalTime,
        )
    }
}
