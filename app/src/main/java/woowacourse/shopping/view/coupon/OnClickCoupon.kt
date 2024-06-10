package woowacourse.shopping.view.coupon

import woowacourse.shopping.domain.model.coupon.Coupon

interface OnClickCoupon {
    fun clickCoupon(coupon: Coupon)

    fun clickPayment()
}
