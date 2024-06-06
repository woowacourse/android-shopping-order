package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

abstract class CouponState {
    abstract val coupon: Coupon

    abstract fun isValidCoupon(carts: List<Cart>): Boolean

    abstract fun calculateDiscount(totalAmount: Int): Int
}
