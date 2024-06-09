package woowacourse.shopping.view.order.adapter

import woowacourse.shopping.domain.model.Coupon

interface OnClickCoupon {
    fun applyCoupon(coupon: Coupon)
}
