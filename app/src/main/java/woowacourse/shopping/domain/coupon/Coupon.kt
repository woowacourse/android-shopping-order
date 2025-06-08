package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: DiscountType

    data class PriceDiscount(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.PRICE_DISCOUNT
    }

    data class Bonus(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.BONUS
    }

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.FREE_SHIPPING
    }

    data class PercentageDiscount(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val availableStartTime: LocalTime,
        val availableEndTime: LocalTime,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.PERCENTAGE_DISCOUNT
    }

    companion object
}
