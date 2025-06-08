package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartItem
import java.time.LocalDateTime

data class PercentageCoupon(override val info: CouponInfo, override val type: CouponType) : Coupon {
    override fun isAvailable(
        cartItems: List<CartItem>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(info.expirationDate)) return false
        if (info.availableTime != null &&
            !info.availableTime.includes(now.toLocalTime())
        ) {
            return false
        }
        return true
    }

    override fun discount(cartItems: List<CartItem>): Int {
        val discountRate = (info.discount ?: 0) / 100.0
        return (cartItems.sumOf(CartItem::totalPrice) * discountRate).toInt()
    }
}
