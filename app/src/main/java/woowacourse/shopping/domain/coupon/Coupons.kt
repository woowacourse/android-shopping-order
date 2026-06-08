package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.CartItems
import java.time.LocalDateTime

data class Coupons(
    val coupons: List<Coupon> = emptyList(),
) {
    fun getApplicableCoupons(
        current: LocalDateTime,
        cartItems: CartItems,
    ): Coupons =
        Coupons(
            coupons = coupons.filter { it.isAvailable(current, cartItems) },
        )

    fun getCouponById(couponId: Int): Coupon? = coupons.find { it.id == couponId }
}
