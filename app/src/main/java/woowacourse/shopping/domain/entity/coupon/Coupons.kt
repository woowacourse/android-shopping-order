package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart
import java.time.LocalDateTime

@JvmInline
value class Coupons(val coupons: List<Coupon> = emptyList()) {
    fun availableCoupons(cart: Cart, targetDateTime: LocalDateTime = LocalDateTime.now()): Coupons {
        return coupons
            .filter { it.available(cart, targetDateTime) }
            .let(::Coupons)
    }

    fun findCouponById(id: Long): Coupon? {
        return coupons.find { it.id == id }
    }
}
