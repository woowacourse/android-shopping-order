package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel

interface PaymentActionHandler {
    fun pay()

    fun checkCoupon(couponUiModel: CouponUiModel)
}