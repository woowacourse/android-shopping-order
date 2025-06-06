package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    suspend fun load(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun getRecentWatchingProducts(size: Int): Result<List<Product>>

    suspend fun getLatestRecentWatchingProduct(): Result<Product?>

    suspend fun getRecentRecommendWatchingProducts(size: Int): Result<List<Product>>

    suspend fun updateRecentWatchingProduct(product: Product): Result<Unit>

    suspend fun getProduct(productId: Long): Result<Product?>
}
