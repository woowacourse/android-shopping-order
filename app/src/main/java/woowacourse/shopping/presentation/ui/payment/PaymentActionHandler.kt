package woowacourse.shopping.presentation.ui.payment

import kotlinx.coroutines.Job
import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel

interface PaymentActionHandler {
    fun pay(): Job

    fun checkCoupon(couponUiModel: CouponUiModel)
}