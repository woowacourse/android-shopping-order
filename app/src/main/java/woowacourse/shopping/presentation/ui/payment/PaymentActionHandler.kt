package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.presentation.model.CouponUiModel

interface PaymentActionHandler {
    fun toggleCoupon(couponUiModel: CouponUiModel)
}
