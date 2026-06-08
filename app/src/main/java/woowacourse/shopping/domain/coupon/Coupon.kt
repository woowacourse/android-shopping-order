package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.CartItems
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun isAvailable(
        current: LocalDateTime,
        cartItems: CartItems,
    ): Boolean

    fun discount(cartItems: CartItems): Int

    data class FixedCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountAmount: Int,
        val minimumAmount: Int,
    ) : Coupon {
        override fun isAvailable(
            current: LocalDateTime,
            cartItems: CartItems,
        ): Boolean {
            if (current.toLocalDate() > expirationDate) return false
            return cartItems.totalPrice >= minimumAmount
        }

        override fun discount(cartItems: CartItems): Int = discountAmount
    }

    data class PercentageCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercentage: Int,
        val availableTime: TimeRange,
    ) : Coupon {
        override fun isAvailable(
            current: LocalDateTime,
            cartItems: CartItems,
        ): Boolean {
            if (current.toLocalDate() > expirationDate) return false
            return availableTime.contains(current.toLocalTime())
        }

        override fun discount(cartItems: CartItems): Int = cartItems.totalPrice * discountPercentage / 100
    }

    data class BuyXGetYCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override fun isAvailable(
            current: LocalDateTime,
            cartItems: CartItems,
        ): Boolean {
            if (current.toLocalDate() > expirationDate) return false
            return cartItems.values.any { it.quantity.value >= buyQuantity + getQuantity }
        }

        override fun discount(cartItems: CartItems): Int {
            val targetItems =
                cartItems.values.filter { it.quantity.value >= buyQuantity + getQuantity }
            if (targetItems.isEmpty()) return 0

            val target = targetItems.maxBy { it.product.price.value }
            return target.product.price.value * getQuantity
        }
    }

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon {
        override fun isAvailable(
            current: LocalDateTime,
            cartItems: CartItems,
        ): Boolean {
            if (current.toLocalDate() > expirationDate) return false
            return cartItems.totalPrice >= minimumAmount
        }

        override fun discount(cartItems: CartItems): Int = SHIPPING_FEE
    }

    companion object {
        const val SHIPPING_FEE = 3000
    }
}

data class TimeRange(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun contains(time: LocalTime): Boolean {
        if (start <= end) return time in start..end

        return time >= start || time <= end
    }
}
