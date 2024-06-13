package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalTime

class PercentageCoupon(override val coupon: Coupon) : CouponState(coupon) {
    override fun isValid(cartItems: List<CartItem>): Boolean {
        val time = LocalTime.now()
        val availableTime = coupon.availableTime ?: return false

        return time in availableTime.start..availableTime.end
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        val orderPrice = cartItems.sumOf { cartItem -> cartItem.totalPrice }
        return (orderPrice * (coupon.discount ?: 0) * 0.01).toInt()
    }
}
