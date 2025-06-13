package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: LocalDate

    abstract fun isAvailable(cartItems: List<CartItem>): Boolean

    abstract fun calculateDiscountAmount(cartItems: List<CartItem>): Int

    data class FixedAmountCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon() {
        override fun isAvailable(cartItems: List<CartItem>): Boolean = cartItems.sumOf { it.totalPrice } >= minimumAmount

        override fun calculateDiscountAmount(cartItems: List<CartItem>): Int = if (isAvailable(cartItems)) discount else 0
    }

    data class BuyXGetYCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon() {
        override fun isAvailable(cartItems: List<CartItem>): Boolean = cartItems.any { it.amount >= (buyQuantity + getQuantity) }

        override fun calculateDiscountAmount(cartItems: List<CartItem>): Int {
            val eligibleItem = findEligibleItem(cartItems) ?: return 0
            val applicableFreeItems =
                eligibleItem.amount / (buyQuantity + getQuantity) * getQuantity
            return applicableFreeItems * eligibleItem.product.price.value
        }

        private fun findEligibleItem(cartItems: List<CartItem>): CartItem? =
            cartItems
                .filter { it.amount >= buyQuantity + getQuantity }
                .maxByOrNull { it.amount }
    }

    data class FreeShippingCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon() {
        override fun isAvailable(cartItems: List<CartItem>): Boolean = cartItems.sumOf { it.totalPrice } >= minimumAmount

        override fun calculateDiscountAmount(cartItems: List<CartItem>): Int = 0
    }

    data class PercentageCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercent: Int,
        val availableTime: TimeRange,
    ) : Coupon() {
        override fun isAvailable(cartItems: List<CartItem>): Boolean = LocalTime.now() in availableTime.start..availableTime.end

        override fun calculateDiscountAmount(cartItems: List<CartItem>): Int =
            cartItems.sumOf { it.totalPrice.toInt() } * discountPercent / 100
    }
}

data class TimeRange(
    val start: LocalTime,
    val end: LocalTime,
)
