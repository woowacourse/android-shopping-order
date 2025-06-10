package woowacourse.shopping.presentation.payment.event

import woowacourse.shopping.domain.model.Coupon

interface CouponEventHandler {
    fun onCouponCheck(coupon: Coupon)
}
