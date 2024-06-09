package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

class PercentageCoupon(override val coupon: Coupon, override val cartItems: List<CartItem>) :
    CouponState {
    override fun isValid(): Boolean {
        return true
    }

    override fun discountPrice(): Int {
        val orderPrice = cartItems.sumOf { cartItem -> cartItem.totalPrice }
        return (orderPrice * (coupon.discount ?: 0) * 0.01).toInt()
    }
}