package woowacourse.shopping.view.order.adapter

import woowacourse.shopping.view.order.model.CouponUiModel

interface OnClickCoupon {
    fun applyCoupon(coupon: CouponUiModel)
}
