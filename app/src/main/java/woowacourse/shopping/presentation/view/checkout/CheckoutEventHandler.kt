package woowacourse.shopping.presentation.view.checkout

import woowacourse.shopping.presentation.model.CouponUiModel

interface CheckoutEventHandler {
    fun onToggleSelection(
        coupon: CouponUiModel,
        isSelected: Boolean,
    )

    fun onFinalizeOrder()
}
