package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.Pageable
import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    suspend fun loadPageableProducts(
        page: Int,
        size: Int,
    ): Result<Pageable<Product>>

    suspend fun getProductById(id: Long): Result<Product?>

    suspend fun loadLatestViewedProduct(): Result<Product?>

    suspend fun loadRecentViewedProducts(): Result<List<Product>>

    suspend fun addViewedProduct(product: Product): Result<Unit>

    suspend fun loadRecommendedProducts(size: Int): Result<List<Product>>
}
