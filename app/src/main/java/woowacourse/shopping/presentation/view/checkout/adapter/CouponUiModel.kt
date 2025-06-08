package woowacourse.shopping.presentation.view.checkout.adapter

import woowacourse.shopping.domain.Coupon

data class CouponUiModel(
    val coupon: Coupon,
    val isSelected: Boolean = false,
)

fun Coupon.toUiModel(): CouponUiModel = CouponUiModel(this)
