package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewmodel.Companion.DEFAULT_DELIVERY_PRICE

class FreeShippingCoupon(override val coupon: Coupon, override val cartItems: List<CartItem>) :
    CouponState {
    override fun isValid(): Boolean {
        return false
    }

    override fun discountPrice(): Int {
        return DEFAULT_DELIVERY_PRICE
    }
}