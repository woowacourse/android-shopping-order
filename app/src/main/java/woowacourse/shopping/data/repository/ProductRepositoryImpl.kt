package woowacourse.shopping.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.local.ProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.result.flatMapCatching
import woowacourse.shopping.data.util.result.mapCatchingDebugLog
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource,
) : ProductRepository {
    override suspend fun fetchProduct(id: Long): Result<Product> =
        productRemoteDataSource.fetchProduct(id).mapCatchingDebugLog { it.toDomain() }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<Product>> =
        productRemoteDataSource.fetchProducts(null, page, size).mapCatchingDebugLog { response ->
            val products = response.content.map { it.toDomain() }
            val hasMore = !response.last
            PageableItem(products, hasMore)
        }

    override suspend fun fetchSuggestionProducts(
        limit: Int,
        excludedProductIds: List<Long>,
    ): Result<List<Product>> =
        productLocalDataSource
            .getRecentViewedProductCategory()
            .flatMapCatching { category ->
                val fetchLimit = limit + excludedProductIds.size
                productRemoteDataSource.fetchProducts(category, 0, fetchLimit)
            }.mapCatchingDebugLog { response ->
                val filteredProducts =
                    response.content.filterSuggestionProducts(excludedProductIds, limit)
                filteredProducts.map { it.toDomain() }
            }

    override suspend fun getRecentProducts(limit: Int): Result<List<Product>> =
        productLocalDataSource.getRecentProducts(limit).mapCatchingDebugLog { entities ->
            val productIds = entities.map { it.productId }
            fetchAllProductsConcurrently(productIds)
        }

    override suspend fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        recentProductLimit: Int,
    ): Result<Unit> =
        productLocalDataSource
            .insertRecentProduct(RecentProductEntity(productId, category))
            .mapCatchingDebugLog { productLocalDataSource.trimToLimit(recentProductLimit) }

    private fun List<ProductResponse>.filterSuggestionProducts(
        excludedProductIds: List<Long>,
        limit: Int,
    ): List<ProductResponse> =
        this
            .filterNot { excludedProductIds.contains(it.id) }
            .take(limit)

    private suspend fun fetchAllProductsConcurrently(productIds: List<Long>): List<Product> =
        coroutineScope {
            val deferredProducts =
                productIds.map { productId -> async { fetchAndConvertToDomain(productId) } }
            deferredProducts.mapNotNull { it.await() }
        }

    private suspend fun fetchAndConvertToDomain(productId: Long): Product? =
        productRemoteDataSource
            .fetchProduct(productId)
            .mapCatchingDebugLog { it.toDomain() }
            .getOrNull()
}
