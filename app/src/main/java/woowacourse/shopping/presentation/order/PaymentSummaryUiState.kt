package woowacourse.shopping.presentation.order

data class PaymentSummaryUiState(
    val orderPrice: Int,
    val couponDiscountPrice: Int,
    val shippingFee: Int,
    val totalPaymentAmount: Int,
    val buyXGetYDiscountProductId: Long? = null,
    val buyXGetYDiscountProductPrice: Int? = null,
)
