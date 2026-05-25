package woowacourse.shopping.ui.payment

import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.coupon.Coupon

sealed interface PaymentUiState {
    object Loading : PaymentUiState

    data class Success(
        val cartItems: CartItems,
        val availableCoupons: List<Coupon> = emptyList(),
        val selectedCoupon: Coupon? = null,
        val subtotal: Int = 0,
        val couponDiscount: Int = 0,
        val shippingFee: Int = 3000,
        val totalPrice: Int = 0,
    ) : PaymentUiState
}
