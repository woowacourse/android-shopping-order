package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart

@JvmInline
value class Coupons(val coupons: List<Coupon>) {
    fun availableCoupons(cart: Cart, shippingFee: Long): Coupons {
        return coupons.filter { !it.isExpired }
            .filter { it.available(cart, shippingFee) }
            .let(::Coupons)
    }
}