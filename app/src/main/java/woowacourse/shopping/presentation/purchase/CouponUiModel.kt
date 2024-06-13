package woowacourse.shopping.presentation.purchase

import com.example.domain.model.coupon.Coupon

data class CouponUiModel(
    val coupon: Coupon,
    val checked: Boolean,
    val available: Boolean,
)
