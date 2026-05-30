package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.order.discount.BuyXGetYDiscountPolicy
import woowacourse.shopping.model.order.discount.DiscountPolicy
import woowacourse.shopping.model.order.discount.FixedDiscountPolicy
import woowacourse.shopping.model.order.discount.FreeShippingDiscountPolicy
import woowacourse.shopping.model.order.discount.PercentageDiscountPolicy
import woowacourse.shopping.model.product.Money
import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: String
    val discountPolicy: DiscountPolicy

    fun isAvailableAt(
        currentDate: LocalDate,
        currentTime: LocalTime,
    ): Boolean {
        val expiration = LocalDate.parse(expirationDate)
        return !currentDate.isAfter(expiration)
    }

    data class FixedDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Money,
        val minimumAmount: Money,
    ) : Coupon {
        override val discountPolicy: DiscountPolicy = FixedDiscountPolicy(discount, minimumAmount)
    }

    data class BuyXGetY(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override val discountPolicy: DiscountPolicy = BuyXGetYDiscountPolicy(buyQuantity, getQuantity)
    }

    data class FreeShipping(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumAmount: Money,
    ) : Coupon {
        override val discountPolicy: DiscountPolicy = FreeShippingDiscountPolicy(minimumAmount)
    }

    data class PercentageDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discountPercentage: Int,
        val availableStartTime: LocalTime,
        val availableEndTime: LocalTime,
    ) : Coupon {
        override val discountPolicy: DiscountPolicy = PercentageDiscountPolicy(discountPercentage)

        override fun isAvailableAt(
            currentDate: LocalDate,
            currentTime: LocalTime,
        ): Boolean {
            if (!super.isAvailableAt(currentDate, currentTime)) return false
            return currentTime in availableStartTime..availableEndTime
        }
    }
}
