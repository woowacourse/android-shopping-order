package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalTime

class Coupons(
    val value: List<Coupon>,
) {
    fun filteredCoupons(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Coupons {
        val filteredCoupons =
            value.filter { coupon ->
                when (coupon) {
                    is BogoCoupon -> carts.any { it.quantity >= 3 }
                    is DiscountCoupon -> carts.sumOf { it.totalPrice } >= coupon.minimumAmount
                    is FreeShippingCoupon -> carts.sumOf { it.totalPrice } >= coupon.minimumAmount
                    is TimeLimitedCoupon -> coupon.availableTime.isAvailable(time)
                }
            }
        return Coupons(filteredCoupons)
    }
}
