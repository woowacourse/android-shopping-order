package woowacourse.shopping.presentation.view.checkout

import woowacourse.shopping.presentation.view.checkout.adapter.CouponUiModel

interface CheckoutEventHandler {
    fun onToggleSelection(
        coupon: CouponUiModel,
        isSelected: Boolean,
    )
}
