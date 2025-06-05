package woowacourse.shopping.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.RecentProductLocalDataSource
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {
    override suspend fun getRecentProducts(limit: Int): Result<List<Product>> =
        runCatchingDebugLog {
            val recentEntities = recentProductLocalDataSource.getRecentProducts(limit).getOrNull()
            val recentProductIds = recentEntities?.map { it.productId } ?: emptyList()

            fetchAllProductsConcurrently(recentProductIds)
        }

    override suspend fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        recentProductLimit: Int,
    ): Result<Unit> =
        runCatchingDebugLog {
            recentProductLocalDataSource.insertRecentProduct(
                RecentProductEntity(productId, category),
            )
            recentProductLocalDataSource.trimToLimit(recentProductLimit)
        }

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
