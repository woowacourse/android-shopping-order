package woowacourse.shopping.presentation.ui.shoppingcart.payment

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.coupons.Coupon

data class PaymentUiState(
    val couponUiStates: List<CouponUiState> = listOf(),
    val orderCarts: List<Cart> = listOf(),
    val discountPrice: Int = 0,
    val deliveryPrice: Int = 3000,
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val totalPaymentPrice get() = orderTotalPrice + discountPrice + deliveryPrice

    val isAnyChecked get() = couponUiStates.count { it.isChecked } > 0
}

data class CouponUiState(
    val coupon: Coupon,
    val isChecked: Boolean,
)
