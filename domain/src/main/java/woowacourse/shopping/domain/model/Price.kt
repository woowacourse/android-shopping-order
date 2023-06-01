package woowacourse.shopping.domain.model

data class Price(val value: Int) {

    init {
        require(value >= 0) { PRICE_NEGATIVE_ERROR }
    }

    operator fun times(count: Int): Price {
        return Price(value * count)
    }

    operator fun minus(price: Int): Price {
        return Price(value - price)
    }

    companion object {
        private const val PRICE_NEGATIVE_ERROR = "상품가격은 음수일 수 없습니다."
    }
}
