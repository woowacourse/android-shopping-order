package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartItem
import java.time.LocalDateTime

interface Coupon {
    val info: CouponInfo
    val type: CouponType

    fun isAvailable(
        cartItems: List<CartItem>,
        now: LocalDateTime,
    ): Boolean

    fun discount(cartItems: List<CartItem>): Long
}
