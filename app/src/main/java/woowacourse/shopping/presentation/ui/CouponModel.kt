package woowacourse.shopping.presentation.ui

import woowacourse.shopping.domain.Coupon

data class CouponModel(
    val coupon: Coupon,
    val isChecked: Boolean,
) {
    companion object {
        fun Coupon.toUiModel() =
            CouponModel(
                coupon = this,
                isChecked = false,
            )
    }
}
