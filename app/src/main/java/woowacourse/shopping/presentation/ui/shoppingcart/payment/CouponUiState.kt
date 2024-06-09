package woowacourse.shopping.presentation.ui.shoppingcart.payment

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.coupons.Coupon

data class CouponUiState(
    val coupons: List<Coupon> = listOf(),
    val orderCarts: List<Cart> = listOf(),
    val discountPrice: Int = 0,
    val deliveryPrice: Int = 3000,
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val totalPaymentPrice get() = orderTotalPrice + discountPrice + deliveryPrice
}
