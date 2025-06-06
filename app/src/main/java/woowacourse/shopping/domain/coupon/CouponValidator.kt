package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDate
import java.time.LocalDateTime

class CouponValidator : CouponValidate {
    override fun validCoupon(
        coupons: List<Coupon>,
        orders: List<ShoppingCart>,
    ): List<Coupon> {
        val today = LocalDate.now()
        val now = LocalDateTime.now()
        val totalPayment = orders.sumOf { it.payment }

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
