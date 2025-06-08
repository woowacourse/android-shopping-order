package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Cart
import java.time.LocalDate
import java.time.LocalDateTime

class Fixed5000Coupon(
    override val couponId: Long,
    override val expirationDate: LocalDate,
    private val minimumOrderPrice: Int,
) : Coupon {
    override fun isAvailable(cart: Cart, current: LocalDateTime): Boolean {
        return cart.totalPrice >= minimumOrderPrice && current.toLocalDate() <= expirationDate
    }
}