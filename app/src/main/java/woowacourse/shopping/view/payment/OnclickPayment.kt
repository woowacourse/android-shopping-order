package woowacourse.shopping.view.payment

import woowacourse.shopping.domain.model.coupon.Coupon

interface OnclickPayment {
    fun clickCoupon(coupon: Coupon)

    fun clickPayment()
}
