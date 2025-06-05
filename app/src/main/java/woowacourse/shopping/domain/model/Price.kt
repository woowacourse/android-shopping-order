package woowacourse.shopping.domain.model

data class Price(
    val original: Int,
    val discount: Int = DEFAULT_DISCOUNT_PRICE,
    val shipping: Int = DEFAULT_SHIPPING_PRICE,
    val result: Int = original,
) {
    companion object {
        private const val DEFAULT_DISCOUNT_PRICE: Int = 0
        private const val DEFAULT_SHIPPING_PRICE: Int = 3_000
    }
}
