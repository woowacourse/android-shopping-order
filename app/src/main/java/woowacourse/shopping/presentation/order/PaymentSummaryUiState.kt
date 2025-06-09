package woowacourse.shopping.presentation.order

data class PaymentSummaryUiState(
    val orderPrice: Int,
    val couponDiscountPrice: Int = 0,
    val shippingFee: Int = 3000,
) {
    val totalPaymentAmount: Int get() = orderPrice + couponDiscountPrice + shippingFee

    fun update(
        couponDiscountPrice: Int,
        shippingFee: Int,
    ): PaymentSummaryUiState =
        PaymentSummaryUiState(
            orderPrice,
            this.couponDiscountPrice + couponDiscountPrice,
            this.shippingFee + shippingFee,
        )
}
