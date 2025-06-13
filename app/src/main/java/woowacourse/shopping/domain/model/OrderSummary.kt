package woowacourse.shopping.domain.model

data class OrderSummary(
    val orderAmount: Long,
    val couponAmount: Int,
    val shippingFee: Int,
) {
    val totalPrice: Long
        get() = (orderAmount - couponAmount + shippingFee)
}
