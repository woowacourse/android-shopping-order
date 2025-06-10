package woowacourse.shopping.presentation.payment.event

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.presentation.payment.PaymentViewModel

class CouponEventHandlerImpl(
    private val viewModel: PaymentViewModel,
) : CouponEventHandler {
    override fun onCouponCheck(coupon: Coupon) {
        viewModel.onCheckCoupon(coupon)
    }
}
