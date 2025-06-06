package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : ProductRepository {
    override suspend fun fetchProduct(id: Long): Result<Product> =
        runCatching {
            productRemoteDataSource.fetchProduct(id).getOrThrow().toDomain()
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<Product>> {
        val response = productRemoteDataSource.fetchProducts(null, page, size).getOrThrow()
        val products = response.content.map { it.toDomain() }
        val hasMore = !response.last
        return runCatching { PageableItem(products, hasMore) }
    }

    override suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>> {
        val category = recentProductLocalDataSource.getRecentViewedProductCategory()
        val response = productRemoteDataSource.fetchProducts(category, null, null).getOrNull()
        val allProducts = response?.content ?: emptyList()
        val filteredProducts = allProducts.filterNot { excludedProductIds.contains(it.id) }
        return runCatching { filteredProducts.take(limit).map { it.toDomain() } }
    }
}
