package woowacourse.shopping.ui.payment

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.ui.model.UiCoupon
import woowacourse.shopping.ui.model.UiPaymentPrice
import woowacourse.shopping.ui.model.mapper.toUiModel

data class PaymentUiState(
    val cartItems: List<CartItem> = listOf(),
    val coupons: List<Coupon> = listOf(),
    val totalPrice: Money = Money(0),
    val paymentPrice: UiPaymentPrice = UiPaymentPrice(),
    val selectedCouponId: Long? = null,
    val isLoading: Boolean = true,
) {
    val uiCoupons: List<UiCoupon>
        get() =
            Payment(cartItems = cartItems)
                .availableCoupons(coupons)
                .map { coupon -> coupon.toUiModel(coupon.id == selectedCouponId) }
}
