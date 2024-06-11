package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.presentation.ui.CouponModel

interface PaymentHandler {
    fun onCouponClicked(couponModel: CouponModel)

    fun onPaymentClicked()
}
