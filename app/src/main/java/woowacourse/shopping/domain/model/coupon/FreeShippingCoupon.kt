package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewmodel.Companion.DEFAULT_DELIVERY_PRICE
import java.time.LocalDate

class FreeShippingCoupon(override val coupon: Coupon) : CouponState(coupon) {
    override fun isValid(cartItems: List<CartItem>): Boolean {
        val now = LocalDate.now()
        if (now > coupon.expirationDate) return false

        val orderPrice = cartItems.sumOf { cartItem -> cartItem.totalPrice }
        return orderPrice >= MIN_ORDER_PRICE_FOR_FREE_SHIPPING_COUPON
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        return DEFAULT_DELIVERY_PRICE
    }

    companion object {
        private const val MIN_ORDER_PRICE_FOR_FREE_SHIPPING_COUPON = 50000
    }
}
