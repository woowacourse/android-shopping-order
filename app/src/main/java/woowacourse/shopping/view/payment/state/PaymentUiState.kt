package woowacourse.shopping.view.payment.state

data class PaymentUiState(
    val coupons: List<CouponUi>,
    val paymentState: PaymentState,
)
