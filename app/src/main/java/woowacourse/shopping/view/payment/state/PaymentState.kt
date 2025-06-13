package woowacourse.shopping.view.payment.state

data class PaymentState(
    val orderPrice: Int,
    val discountPrice: Int,
    val shippingPrice: Int,
    val totalPrice: Int,
)
