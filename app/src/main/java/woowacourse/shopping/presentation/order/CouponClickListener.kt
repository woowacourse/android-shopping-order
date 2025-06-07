package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.model.CouponUiModel

interface CouponClickListener {
    fun onClickSelect(coupon: CouponUiModel)
}
