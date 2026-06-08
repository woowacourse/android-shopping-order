package woowacourse.shopping.domain.order

import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.coupon.Coupon

data class Order(
    val items: CartItems = CartItems(),
    val coupon: Coupon? = null,
) {
    val totalPrice: Int = items.totalPrice
    val discountAmount: Int = coupon?.discount(items) ?: 0
    val amountToPay: Int = maxOf(totalPrice - discountAmount + Coupon.SHIPPING_FEE)
}
