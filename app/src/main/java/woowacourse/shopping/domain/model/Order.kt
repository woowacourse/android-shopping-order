package woowacourse.shopping.domain.model

data class Order(
    val cartProducts: List<CartProduct>,
    private val coupon: Coupon? = null,
) {
    val orderPrice: Int = cartProducts.sumOf { it.totalPrice }

    val discountAmount: Int = coupon?.discountType?.calculateDiscount(cartProducts, SHIPPING_FEE) ?: 0

    val paymentAmount: Int = orderPrice + SHIPPING_FEE - discountAmount

    companion object {
        const val SHIPPING_FEE = 3_000
    }
}
