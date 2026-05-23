package woowacourse.shopping.presentation.payment.model

data class PaymentUiState(
    val availableCoupons: List<CouponUiModel> = emptyList(),
    val selectedCouponId: Long? = null,
    val orderAmount: Long = 0,
    val discountAmount: Long = 0,
    val deliveryFee: Int = 0,
    val totalAmount: Long = 0,
)
