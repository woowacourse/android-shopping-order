package woowacourse.shopping.view.payment.adapter

import woowacourse.shopping.domain.model.coupon.Coupon

fun interface CouponSelectListener {
    fun onSelectCoupon(coupon: Coupon)
}
