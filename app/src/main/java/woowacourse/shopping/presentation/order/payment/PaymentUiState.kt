package woowacourse.shopping.presentation.order.payment

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.coupon.DiscountResult

data class PaymentUiState(
    val cart: Cart = Cart(),
    val coupons: List<CouponUiModel> = emptyList(),
    val discountResult: DiscountResult = DiscountResult(0, 0, 0),
) {
    val selectedCoupon: CouponUiModel? get() = coupons.find { it.isSelected }
    val totalPrice: Long get() = cart.totalPrice()
    val totalOrderIds
        get() = cart.cartProducts.map { it.product.id }
}
