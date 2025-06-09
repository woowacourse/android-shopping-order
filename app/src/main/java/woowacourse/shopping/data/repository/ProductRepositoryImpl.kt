package woowacourse.shopping.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.local.ProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource,
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
            val category = productLocalDataSource.getRecentViewedProductCategory().getOrNull()

            val fetchLimit = limit + excludedProductIds.size
            val response =
                productRemoteDataSource.fetchProducts(category, 0, fetchLimit).getOrNull()
            val allProducts = response?.content ?: emptyList()
            val filteredProducts = allProducts.filterSuggestionProducts(excludedProductIds, limit)
            filteredProducts.map { it.toDomain() }
        }

    override suspend fun getRecentProducts(limit: Int): Result<List<Product>> =
        runCatchingDebugLog {
            val recentEntities = productLocalDataSource.getRecentProducts(limit).getOrNull()
            val recentProductIds = recentEntities?.map { it.productId } ?: emptyList()

            fetchAllProductsConcurrently(recentProductIds)
        }

    override suspend fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        recentProductLimit: Int,
    ): Result<Unit> =
        runCatchingDebugLog {
            val entity = RecentProductEntity(productId, category)
            productLocalDataSource.insertRecentProduct(entity)
            productLocalDataSource.trimToLimit(recentProductLimit)
        }

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
            .getOrNull()
            ?.toDomain()
}
