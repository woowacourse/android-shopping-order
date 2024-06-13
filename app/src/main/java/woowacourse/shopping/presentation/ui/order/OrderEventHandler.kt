package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.domain.model.coupon.CouponState

interface OrderEventHandler {
    fun toggleCoupon(couponState: CouponState)
}
