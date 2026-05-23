package woowacourse.shopping.ui.payment

import woowacourse.shopping.model.cart.CartItem
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.order.Payment

data class PaymentUiState(
    val isLoading: Boolean = false,

    val items: List<CartItem> = emptyList(),
    val availableCoupons: List<Coupon> = emptyList(),
    val selectedCouponId: Long? = null,
    val payment: Payment = Payment.EMPTY,
)
