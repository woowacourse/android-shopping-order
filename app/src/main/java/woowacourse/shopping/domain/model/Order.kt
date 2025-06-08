package woowacourse.shopping.domain.model

class Order(
    products: List<CartProduct>,
    coupon: Coupon? = null,
) {
    val orderPrice: Int = products.sumOf { it.totalPrice }

    val discountAmount: Int = coupon?.discountType?.calculateDiscount(products, SHIPPING_FEE) ?: 0

    val paymentAmount: Int = orderPrice + SHIPPING_FEE - discountAmount

    companion object {
        const val SHIPPING_FEE = 3_000
    }
}
