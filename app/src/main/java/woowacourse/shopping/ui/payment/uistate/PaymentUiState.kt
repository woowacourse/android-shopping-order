package woowacourse.shopping.ui.payment.uistate

data class PaymentUiState(
    val coupons: List<CouponUiModel> = emptyList(),
    val orderAmount: String = "0원",
    val couponDiscountAmount: String = "0원",
    val deliveryFee: String = "3,000원",
    val totalPaymentAmount: String = "0원",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
