package woowacourse.shopping.view.recommend

import woowacourse.shopping.domain.product.Product

data class RecommendProduct(
    var quantity: Int,
    private val product: Product,
) {
    val productId = product.id
    val name = product.name
    val price = product.price
    val imageUrl = product.imageUrl
}
