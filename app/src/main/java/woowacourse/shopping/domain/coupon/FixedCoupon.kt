package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Cart
import java.time.LocalDate
import java.time.LocalDateTime

class FixedCoupon(
    override val couponId: Long,
    override val expirationDate: LocalDate,
    private val disCountPrice: Int,
    private val minimumOrderPrice: Int,
) : Coupon {
    override fun isAvailable(cart: Cart, current: LocalDateTime): Boolean {
        return cart.totalPrice >= minimumOrderPrice && current.toLocalDate() <= expirationDate
    }

    override fun discountPrice(cart: Cart): Int {
        return disCountPrice.coerceAtMost(cart.totalPrice)
    }
}