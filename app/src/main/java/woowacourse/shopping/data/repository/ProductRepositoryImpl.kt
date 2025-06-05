package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.RecentProductLocalDataSource
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : ProductRepository {
    override suspend fun fetchProduct(id: Long): Result<Product> =
        runCatchingDebugLog {
            productRemoteDataSource.fetchProduct(id).getOrThrow().toDomain()
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<Product>> =
        runCatchingDebugLog {
            val response = productRemoteDataSource.fetchProducts(null, page, size).getOrThrow()
            val products = response.content.map { it.toDomain() }
            val hasMore = !response.last
            PageableItem(products, hasMore)
        }

    override suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>> =
        runCatchingDebugLog {
            val category = recentProductLocalDataSource.getRecentViewedProductCategory().getOrNull()

            val fetchLimit = limit + excludedProductIds.size
            val response =
                productRemoteDataSource.fetchProducts(category, 0, fetchLimit).getOrNull()
            val allProducts = response?.content ?: emptyList()

            val filteredProducts =
                allProducts
                    .filterNot { excludedProductIds.contains(it.id) }
                    .take(limit)

            filteredProducts.map { it.toDomain() }
        }
}
