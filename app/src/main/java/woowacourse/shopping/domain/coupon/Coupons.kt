package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDateTime

class Coupons(
    private val coupons: List<Coupon>,
) {
    fun getAvailable(
        cartItems: List<ShoppingCart>,
        now: LocalDateTime
    ): List<Coupon> {
        return coupons.filter { it.isAvailable(cartItems, now) }
    }
}
