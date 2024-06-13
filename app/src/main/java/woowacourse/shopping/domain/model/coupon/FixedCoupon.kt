package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalDate

class FixedCoupon(override val coupon: Coupon) : CouponState(coupon) {
    override fun isValid(cartItems: List<CartItem>): Boolean {
        val now = LocalDate.now()
        if (now > coupon.expirationDate) return false

        val orderPrice = cartItems.sumOf { cartItem -> cartItem.totalPrice }
        return orderPrice >= MIN_ORDER_PRICE_FOR_FIXED_COUPON
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        return coupon.discount ?: 0
    }

    companion object {
        private const val MIN_ORDER_PRICE_FOR_FIXED_COUPON = 100000
    }
}
