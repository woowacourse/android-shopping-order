package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.domain.Coupon

interface PaymentHandler {
    fun onCouponClicked(coupon: Coupon)

    fun onPaymentClicked()
}
