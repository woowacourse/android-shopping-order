package woowacourse.shopping.view.recommend

import woowacourse.shopping.domain.product.Product

data class RecommendProduct(
    private val product: Product,
    val quantity: Int = 0,
) {
    val id = product.id
    val name = product.name
    val price = product.price
    val imageUrl = product.imageUrl
}
