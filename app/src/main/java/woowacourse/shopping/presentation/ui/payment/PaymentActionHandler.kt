package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.domain.model.coupon.CouponState

interface PaymentActionHandler {
    fun toggleCoupon(couponState: CouponState)
}
