package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDateTime

class AvailableCoupons(
    private val coupons: List<Coupon>,
    private val cartItems: List<ShoppingCart>,
    private val now: LocalDateTime,
) {
    fun get(): List<Coupon> {
        return coupons.filter { it.isAvailable(cartItems, now) }
    }
}
