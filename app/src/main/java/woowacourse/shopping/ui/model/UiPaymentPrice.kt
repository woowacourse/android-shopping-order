package woowacourse.shopping.ui.model

data class UiPaymentPrice(
    val totalPrice: Long = 0,
    val deliveryFee: Long = 0,
    val couponDiscountPrice: Long = 0,
    val paymentPrice: Long = 0,
)
