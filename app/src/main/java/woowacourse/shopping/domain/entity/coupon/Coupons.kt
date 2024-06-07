package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart

@JvmInline
value class Coupons(val coupons: List<Coupon>) {
    fun availableCoupons(cart: Cart, shippingFee: Long): List<Coupon> {
        return coupons.filter { !it.isExpired }
            .filter { it.available(cart, shippingFee) }
    }
}