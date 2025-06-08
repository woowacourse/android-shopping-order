package woowacourse.shopping.presentation.payment.event

import woowacourse.shopping.data.model.Coupon

interface CouponEventHandler {
    fun onCouponCheck(coupon: Coupon)
}