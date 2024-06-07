package woowacourse.shopping.ui.payment

import woowacourse.shopping.data.coupon.CouponState

data class CouponUiModel(
    val couponState: CouponState,
    val isChecked: Boolean = false,
)

fun CouponState.toUiModel() = CouponUiModel(couponState = this)
