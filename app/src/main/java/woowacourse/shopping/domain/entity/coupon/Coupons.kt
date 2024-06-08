package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart

@JvmInline
value class Coupons(val coupons: List<Coupon>) {
    fun availableCoupons(cart: Cart): Coupons {
        return coupons.filter { !it.isExpired }
            .filter { it.available(cart) }
            .let(::Coupons)
    }

    fun findCouponById(id: Long): Coupon? {
        return coupons.find { it.id == id }
    }
}
