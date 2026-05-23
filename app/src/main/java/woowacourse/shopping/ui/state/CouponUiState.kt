package woowacourse.shopping.ui.state

import woowacourse.shopping.model.coupon.Coupon

data class CouponUiState(
    val coupons: List<Coupon> = emptyList(),
    val selectedCouponId: Long? = null,
    val orderAmount: Int = 0,
    val discountAmount: Int = 0,
    val shippingFee: Int = 3_000,
    val totalPaymentAmount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)