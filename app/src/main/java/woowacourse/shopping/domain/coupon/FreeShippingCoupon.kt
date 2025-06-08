package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartItem
import java.time.LocalDateTime

class FreeShippingCoupon(
    override val info: CouponInfo,
    override val type: CouponType,
) : Coupon {
    override fun isAvailable(
        cartItems: List<CartItem>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(info.expirationDate)) return false
        if (info.minimumAmount != null &&
            cartItems.sumOf { cartItem -> cartItem.totalPrice } < info.minimumAmount
        ) {
            return false
        }
        return true
    }

    override fun discount(cartItems: List<CartItem>): Int = SHIPPING_COST

    companion object {
        const val SHIPPING_COST = 3_000
    }
}
