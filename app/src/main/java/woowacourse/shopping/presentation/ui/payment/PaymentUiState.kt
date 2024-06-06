package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.coupon.CouponState

data class PaymentUiState(
    val couponsState: List<CouponState> = emptyList(),
    val orderCarts: List<Cart> = emptyList(),
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val couponDiscountPrice
        get() = couponsState.firstOrNull { it.coupon.checked }?.calculateDiscount(orderTotalPrice) ?: 0

    val paymentTotalPrice get() = orderCarts.sumOf { it.totalPrice } + DELIVERY_FEE_AMOUNT - couponDiscountPrice

    companion object {
        const val DELIVERY_FEE_AMOUNT = 3_000
    }
}
