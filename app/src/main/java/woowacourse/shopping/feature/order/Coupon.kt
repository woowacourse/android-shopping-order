package woowacourse.shopping.feature.order

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val code: String
    abstract val expiry: LocalDate
    abstract fun calculateDiscount(cartItems: List<CartItem>, orderAmount: Int): Int

    data class Fixed5000(val minAmount: Int = 100_000) : Coupon() {
        override val code = "FIXED5000"
        override val expiry = LocalDate.of(2024, 11, 30)

        override fun calculateDiscount(cartItems: List<CartItem>, orderAmount: Int): Int {
            return if (orderAmount >= minAmount) 5_000 else 0
        }
    }

    object BOGO : Coupon() {
        override val code = "BOGO"
        override val expiry = LocalDate.of(2024, 5, 30)

        override fun calculateDiscount(cartItems: List<CartItem>, orderAmount: Int): Int {
            val eligible = cartItems.filter { it.quantity >= 3 }
            val discountedItem = eligible.maxByOrNull { it.goods.price }
            return discountedItem?.goods?.price ?: 0
        }
    }

    data class FreeShipping(val minAmount: Int = 50_000) : Coupon() {
        override val code = "FREESHIPPING"
        override val expiry = LocalDate.of(2024, 8, 31)

        override fun calculateDiscount(cartItems: List<CartItem>, orderAmount: Int): Int {
            return if (orderAmount >= minAmount) SHIPPING_FEE else 0
        }

        companion object {
            const val SHIPPING_FEE = 3_000
        }
    }

    object MiracleSale : Coupon() {
        override val code = "MIRACLESALE"
        override val expiry = LocalDate.of(2024, 7, 31)

        override fun calculateDiscount(cartItems: List<CartItem>, orderAmount: Int): Int {
            val now = LocalTime.now()
            return if (now in LocalTime.of(4, 0)..LocalTime.of(7, 0)) {
                (orderAmount * 0.3).toInt()
            } else 0
        }
    }
}