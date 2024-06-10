package woowacourse.shopping.presentation.cart.model

import com.example.domain.model.Coupon

data class CouponUiModel(
    val coupon: Coupon,
    val checked: Boolean,
    val available: Boolean,
)
