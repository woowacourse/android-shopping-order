package woowacourse.shopping.domain.model

data class Price(
    val orderPrice: Int = 0,
    val discountPrice: Int = 0,
    val shippingFee: Int = 3_000,
    val totalPrice: Int = 0,
) {
    companion object {
        val DEFAULT_PRICE = Price()
        const val DEFAULT_SHIPPING_FEE: Int = 3000
        const val DEFAULT_DISCOUNT: Int = 0
    }
}
