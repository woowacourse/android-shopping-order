package woowacourse.shopping.domain.model

import java.io.Serializable

data class Product(
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val category: String = "",
) : Serializable {
    init {
        require(price >= MINIMUM_PRODUCT_PRICE) { INVALID_PRICE_MESSAGE }
    }

    companion object {
        private const val INVALID_PRICE_MESSAGE = "가격은 0이상 이어야 합니다."
        private const val MINIMUM_PRODUCT_PRICE = 0
    }
}
