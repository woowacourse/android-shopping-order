package woowacourse.shopping.data.product

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getProducts(
        page: Int,
        size: Int,
    ): List<Product>

    fun find(id: Long): Product

    fun productsByCategory(category: String): List<Product>
}
