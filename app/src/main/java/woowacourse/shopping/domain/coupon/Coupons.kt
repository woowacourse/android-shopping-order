package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Receipt
import java.time.LocalDateTime

class Coupons(private val coupons: List<Coupon>) {
    fun applyApplicableCoupons(receipt: Receipt, current: LocalDateTime): List<Coupon> {
        return coupons
            .filter { it.isAvailable(receipt, current) }
    }
}
