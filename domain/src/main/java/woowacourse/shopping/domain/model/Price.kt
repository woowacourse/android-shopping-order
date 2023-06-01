package woowacourse.shopping.domain.model

data class Price(val value: Int) {

    init {
        require(value >= MINIMUM_PRICE_VALUE) { PRICE_NEGATIVE_ERROR }
    }

    operator fun times(count: Int): Price {
        return Price(value * count)
    }

    operator fun minus(price: Int): Price {
        return Price((value - price).coerceAtLeast(MINIMUM_PRICE_VALUE))
    }

    companion object {
        private const val MINIMUM_PRICE_VALUE = 0
        private const val PRICE_NEGATIVE_ERROR = "상품가격은 음수일 수 없습니다."
    }
}
