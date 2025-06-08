package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDateTime

sealed interface Coupon {
    fun calculateDiscount(cartItems: List<ShoppingCart>): Int

    fun isAvailable(cartItems: List<ShoppingCart>, now: LocalDateTime): Boolean
}
