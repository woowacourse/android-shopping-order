package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCarts
import java.time.LocalDateTime

fun interface CouponValidate {
    fun validCoupon(
        now : LocalDateTime,
        coupons: List<Coupon>,
        orders: ShoppingCarts,
    ): List<Coupon>
}
