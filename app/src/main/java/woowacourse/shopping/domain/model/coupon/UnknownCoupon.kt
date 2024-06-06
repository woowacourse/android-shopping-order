package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

class UnknownCoupon(override val coupon: Coupon) : CouponState() {
    override fun createState(coupon: Coupon): CouponState = UnknownCoupon(coupon)

    override fun isValidCoupon(carts: List<Cart>): Boolean = false

    override fun calculateDiscount(totalAmount: Int): Int = 0
}
