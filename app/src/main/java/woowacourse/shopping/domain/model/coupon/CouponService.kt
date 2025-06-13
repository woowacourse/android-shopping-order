package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartsPrice

interface CouponService {
    fun isValid(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean

    fun applyCoupon(
        coupon: Coupon,
        originalCartPrice: CartsPrice,
        cartItems: List<CartItem>,
    ): CartsPrice
}
