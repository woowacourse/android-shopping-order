package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    fun fetchCatalogProduct(productId: Long): Product?

    fun fetchCatalogProducts(productIds: List<Int>): List<Product>

    fun fetchProducts(
        page: Int,
        size: Int,
    ): Products
}
