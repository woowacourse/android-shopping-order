package woowacourse.shopping.presentation.view.order.payment

import woowacourse.shopping.presentation.model.CouponUiModel

interface CouponStateEventListener {
    fun onSelectCoupon(coupon: CouponUiModel)
}
