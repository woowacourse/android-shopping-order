package woowacourse.shopping.domain.model

data class OrderSummary(
    val orderAmount: Int,
    val couponAmount: Int,
    val shippingFee: Int,
) {
    val totalPrice: Int
        get() = orderAmount - couponAmount + shippingFee
}
