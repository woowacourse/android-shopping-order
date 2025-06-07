package woowacourse.shopping.domain.model

class PaymentDetail(
    private val selectedProducts: List<CartProduct>,
    val couponDiscount: Int = DEFAULT_COUPON_DISCOUNT,
    val deliveryFee: Int = DEFAULT_DELIVERY_FEE,
) {
    val orderAmount: Int get() = selectedProducts.sumOf { it.totalPrice }
    val totalPayment: Int get() = orderAmount - couponDiscount + deliveryFee

    companion object {
        private const val DEFAULT_COUPON_DISCOUNT = 0
        private const val DEFAULT_DELIVERY_FEE = 3000
    }
}
