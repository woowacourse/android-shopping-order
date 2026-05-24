package woowacourse.shopping.ui.model

data class UiPaymentPrice(
    val totalPrice: Long,
    val deliveryFee: Long,
    val couponDiscountPrice: Long,
    val paymentPrice: Long,
)
