package woowacourse.shopping.domain.product

import java.io.Serializable

@JvmInline
value class Price(val value: Int) : Serializable {
    init {
        require(value >= 0) { INVALID_VALUE }
    }

    companion object {
        private const val INVALID_VALUE = "가격은 0원 이상이다"
    }
}
