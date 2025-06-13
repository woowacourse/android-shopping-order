package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.coupon.Coupon

data class CouponUiModel(
    val coupon: Coupon,
    val isSelected: Boolean = false,
)

fun Coupon.toUiModel(): CouponUiModel = CouponUiModel(this)
