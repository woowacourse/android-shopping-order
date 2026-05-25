package woowacourse.shopping.ui.payment

import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Coupon

data class PaymentUiState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val coupons: List<Coupon> = emptyList(),
    val selectedCouponId: Long? = null,
) {
    val orderAmount: Long = items.sumOf { it.totalPrice.value }
    val shippingFee: Long = DEFAULT_SHIPPING_FEE
    val discountAmount: Long =
        coupons
            .firstOrNull { it.id == selectedCouponId }
            ?.applicableDiscount(items = items, shippingFee = shippingFee)
            ?: 0
    val totalAmount: Long = (orderAmount + shippingFee - discountAmount).coerceAtLeast(0)

    companion object {
        const val DEFAULT_SHIPPING_FEE = 3_000L
    }
}
