package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCarts
import java.time.LocalDateTime

class CouponValidator : CouponValidate {
    override fun validCoupon(
        coupons: List<Coupon>,
        orders: ShoppingCarts,
    ): List<Coupon> {
        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val totalPayment = orders.totalPayment

        return coupons.filter { coupon ->
            when (coupon) {
                is BogoCoupon -> coupon.isUsable(today, orders)
                is FixedCoupon -> coupon.isUsable(today, totalPayment)
                is FreeshippingCoupon -> coupon.isUsable(today, totalPayment)
                is MiracleSaleCoupon -> coupon.isUsable(now)
            }
        }
    }
}
