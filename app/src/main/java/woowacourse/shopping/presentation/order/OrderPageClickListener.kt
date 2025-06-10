package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.model.CouponUiModel

interface OrderPageClickListener {
    fun onSelectCoupon(coupon: CouponUiModel)

    fun onClickOrder()
}
