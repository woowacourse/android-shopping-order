package woowacourse.shopping.ui.payment

import woowacourse.shopping.domain.model.coupon.CouponState

data class CouponUiModel(
    val couponState: CouponState,
    val isChecked: Boolean = false,
)
