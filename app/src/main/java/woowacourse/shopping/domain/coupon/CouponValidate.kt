package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCarts

fun interface CouponValidate {
    fun validCoupon(
        coupons: List<Coupon>,
        orders: ShoppingCarts,
    ): List<Coupon>
}
