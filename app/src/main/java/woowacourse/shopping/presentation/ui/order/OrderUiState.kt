package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.CouponState

data class OrderUiState(
    val couponsState: List<CouponState> = emptyList(),
    val orderCarts: List<CartItem> = emptyList(),
) {
    val totalPrice: Int
        get() = orderCarts.sumOf { it.totalPrice.toInt() }

    val couponDiscountPrice
        get() = couponsState.firstOrNull { it.coupon.checked }?.calculateDiscount(totalPrice) ?: 0

    val paymentTotalPrice
        get() = orderCarts.sumOf { it.totalPrice } + DELIVERY_FEE_AMOUNT - couponDiscountPrice

    companion object {
        const val DELIVERY_FEE_AMOUNT = 3_000L
    }
}
