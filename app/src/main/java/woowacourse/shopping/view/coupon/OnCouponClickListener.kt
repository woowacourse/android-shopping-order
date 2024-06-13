package woowacourse.shopping.view.coupon

import woowacourse.shopping.domain.model.coupon.Coupon

interface OnCouponClickListener {
    fun clickCoupon(coupon: Coupon)

    fun clickOrder()
}
