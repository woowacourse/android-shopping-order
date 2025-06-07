package woowacourse.shopping.domain.model

data class Price(
    val original: Int,
    val discount: Int = MINIMUM_PRICE,
    val shipping: Int = DEFAULT_SHIPPING_PRICE,
) {
    val result: Int get() = (original - discount + shipping).coerceAtLeast(MINIMUM_PRICE)

    companion object {
        const val MINIMUM_PRICE: Int = 0
        const val DEFAULT_SHIPPING_PRICE: Int = 3_000
        val EMPTY_PRICE: Price = Price(MINIMUM_PRICE)
    }
}
