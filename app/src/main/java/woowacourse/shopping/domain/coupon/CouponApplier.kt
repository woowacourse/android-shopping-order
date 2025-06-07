package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCart

interface CouponApplier<T : Coupon>

fun interface DefaultCouponApplier<T : Coupon> : CouponApplier<T> {
    fun apply(origin: Payment): Payment
}

fun interface OrderBasedCouponApplier<T : Coupon> : CouponApplier<T> {
    fun apply(
        origin: Payment,
        order: List<ShoppingCart>,
        coupon: T,
    ): Payment
}

fun interface CouponBasedCouponApplier<T : Coupon> : CouponApplier<T> {
    fun apply(
        origin: Payment,
        coupon: T,
    ): Payment
}
