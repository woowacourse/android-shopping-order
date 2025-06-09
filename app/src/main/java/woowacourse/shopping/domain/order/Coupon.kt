package woowacourse.shopping.domain.order

import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: DiscountType
    val isExpiration: Boolean
        get() = expirationDate.isBefore(LocalDate.now())

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
        val discountPercentage: Int,
        val availableStartTime: LocalTime,
        val availableEndTime: LocalTime,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.PERCENTAGE_DISCOUNT
        override val isExpiration: Boolean
            get() = expirationDate.isBefore(LocalDate.now())
    }
}
