package woowacourse.shopping.domain.model

data class PaymentSummary(
    val products: List<CartProduct>,
    val discountPrice: Int = DEFAULT_DISCOUNT_PRICE,
    val deliveryFee: Int = DEFAULT_DELIVERY_FEE,
) {
    val orderPrice: Int = products.sumOf { it.totalPrice }

    val finalPaymentPrice: Int = orderPrice - discountPrice + deliveryFee

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3000
        private const val DEFAULT_DISCOUNT_PRICE = 0
    }
}
