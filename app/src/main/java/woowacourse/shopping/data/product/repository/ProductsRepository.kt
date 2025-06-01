package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    suspend fun load(
        page: Int,
        size: Int,
    ): List<Product>

    suspend fun getRecentWatchingProducts(size: Int): List<Product>

    suspend fun getRecentRecommendWatchingProducts(size: Int): List<Product>

    suspend fun updateRecentWatchingProduct(product: Product)

    suspend fun getProduct(productId: Long): Product?
}
