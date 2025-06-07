package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart

fun interface CouponValidate {
    fun validCoupon(
        coupons: List<Coupon>,
        orders: List<ShoppingCart>,
    ): List<Coupon>
}
