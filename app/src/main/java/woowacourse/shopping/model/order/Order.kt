package woowacourse.shopping.model.order

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.cart.CartItem
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.CouponContext
import java.time.Clock

data class Order(
    val items: List<CartItem>,
    val selectedCoupon: Coupon? = null,
    val shippingFee: Money = BASE_SHIPPING_FEE,
) {
    fun couponContext(clock: Clock): CouponContext =
        CouponContext(
            items = items,
            subtotal = items.fold(Money(0)) { acc, item -> acc + item.totalPrice },
            shippingFee = shippingFee,
            clock = clock,
        )

    companion object {
        val BASE_SHIPPING_FEE = Money(3000)
    }
}
