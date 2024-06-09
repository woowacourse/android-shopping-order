package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

class FixedCoupon(override val coupon: Coupon, override val cartItems: List<CartItem>) :CouponState{
    override fun isValid(): Boolean {
        return false
    }

    override fun discountPrice(): Int {
        return coupon.discount ?: 0
    }
}