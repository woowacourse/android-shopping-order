package woowacourse.shopping.feature.goods

import woowacourse.shopping.domain.model.Product

data class GoodsProduct(
    val cartId: Long? = null,
    val product: Product,
    val quantity: Int = 0,
) {
    fun hasQuantity() = quantity > 0
}
