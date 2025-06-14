package woowacourse.shopping.domain

@JvmInline
value class Price(val value: Long) {
    init {
        require(value >= MIN_PRICE) { INVALID_PRICE_MESSAGE }
    }

    companion object {
        const val SHIPPING_FEE = 3_000L
        private const val MIN_PRICE = 0
        private const val INVALID_PRICE_MESSAGE = "최소 금액은 $MIN_PRICE 입니다."
    }
}
