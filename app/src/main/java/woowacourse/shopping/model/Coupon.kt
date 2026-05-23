package woowacourse.shopping.model

import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: String
    val code: String
    val description: String
    val expirationDate: LocalDate

    data class Fixed(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Money,
        val minimumAmount: Money,
    ) : Coupon

    data class Percentage(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercent: Int,
        val availableTime: AvailableTime,
    ) : Coupon

    data class BuyXGetY(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShipping(
        override val id: String,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Money,
    ) : Coupon
}

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)
