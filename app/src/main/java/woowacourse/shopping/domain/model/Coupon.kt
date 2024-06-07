package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon(
    open val id: Long,
    open val code: String,
    open val description: String,
    open val expirationDate: LocalDate,
    open val discountType: String,
) {
    data class FixedDiscountCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon(id, code, description, expirationDate, discountType)

    data class BogoCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon(id, code, description, expirationDate, discountType)

    data class FreeShippingCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val minimumAmount: Int,
    ) : Coupon(id, code, description, expirationDate, discountType)

    data class TimeBasedDiscountCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val availableTimeStart: LocalTime,
        val availableTimeEnd: LocalTime,
    ) : Coupon(id, code, description, expirationDate, discountType)
}
