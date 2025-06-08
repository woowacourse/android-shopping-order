package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.PagedProducts
import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    suspend fun loadPagedProducts(
        page: Int,
        size: Int,
    ): PagedProducts

    suspend fun loadProductsByCategory(category: String): List<Product>

    suspend fun getProductById(id: Long): Product?

    suspend fun loadLatestViewedProduct(): Product?

    suspend fun loadRecentViewedProducts(): List<Product>

    suspend fun addViewedProduct(product: Product)
}
