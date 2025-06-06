package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.entity.toEntity
import woowacourse.shopping.data.util.toLocalDateTime
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val localDataSource: RecentProductLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource,
) : RecentProductRepository {
    override suspend fun getLastViewedProduct(): Result<RecentProduct?> =
        withContext(Dispatchers.IO) {
            localDataSource.getLastViewedProduct().mapCatching { entity ->
                if (entity != null) {
                    val product =
                        productRemoteDataSource.getProductById(entity.productId).getOrThrow()
                    RecentProduct(product, entity.viewedAt.toLocalDateTime())
                } else {
                    null
                }
            }
        }

    override suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProduct>> =
        withContext(Dispatchers.IO) {
            localDataSource.getPagedProducts(limit, offset).mapCatching { entities ->
                val productIds = entities.map { it.productId }
                val products = productRemoteDataSource.getProductsByIds(productIds).getOrThrow()
                val productMap = products.associateBy { it.id }
                entities.mapNotNull { entity ->
                    productMap[entity.productId]?.let { product ->
                        RecentProduct(product, entity.viewedAt.toLocalDateTime())
                    }
                }
            }
        }

    override suspend fun replaceRecentProduct(recentProduct: RecentProduct): Result<Unit> =
        withContext(Dispatchers.IO) {
            localDataSource.replaceRecentProduct(recentProduct.toEntity())
        }
}
