package woowacourse.shopping.view.payment

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.payment.Coupon

data class CouponApplyState(
    val loading: Boolean,
    val coupons: List<CouponsItem>,
    val selectedCoupon: Coupon?,
    val cartItems: List<CartItem>,
    val deliveryFee: Int,
) {
    val orderAmount: Int = cartItems.sumOf(CartItem::price)
    val discountAmount: Int = selectedCoupon?.discountAmount(cartItems, deliveryFee) ?: 0
    val totalPaymentAmount: Int = orderAmount - discountAmount + deliveryFee

    companion object {
        val Loading: CouponApplyState =
            CouponApplyState(
                loading = true,
                coupons = emptyList(),
                selectedCoupon = null,
                cartItems = emptyList(),
                deliveryFee = 0,
            )
    }
}
