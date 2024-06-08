package woowacourse.shopping.presentation.order.payment

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.coupon.Coupons

data class PaymentUiState(
    val cart: Cart = Cart(),
    val coupons: Coupons = Coupons(emptyList()),
    val couponUis: List<CouponUiModel> = emptyList(),
    val shippingFee: Long = 0,
    val discountPrice: Long = 0,
    val paymentPrice: Long = 0,
) {
    val selectedCoupon: CouponUiModel? get() = couponUis.find { it.isSelected }
    val totalPrice: Long get() = cart.totalPrice()
    val totalOrderIds
        get() = cart.cartProducts.map { it.product.id }
}
