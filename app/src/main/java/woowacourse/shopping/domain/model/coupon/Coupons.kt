package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalTime

class Coupons(
    val value: List<Coupon>,
) {
    fun filteredCoupons(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Coupons = Coupons(value.filter { it.isApplicable(carts, time) })
}
