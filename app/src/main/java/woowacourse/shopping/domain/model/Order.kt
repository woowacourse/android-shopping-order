package woowacourse.shopping.domain.model

data class Order(
    val cartProducts: CartProducts,
    private val coupon: Coupon? = null,
) {
    val orderAmount: Int = cartProducts.totalPrice

    val discountAmount: Int =
        coupon?.discount?.calculateDiscount(cartProducts, SHIPPING_FEE) ?: MINIMUM_AMOUNT

    val paymentAmount: Int = orderAmount + SHIPPING_FEE - discountAmount

    companion object {
        const val SHIPPING_FEE = 3_000
        private const val MINIMUM_AMOUNT = 0
    }
}
