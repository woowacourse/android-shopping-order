package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.domain.coupon.Coupon

interface CouponActionHandler {
    fun order()

    fun onCouponClick(selectedCoupon: Coupon)
}
