package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.entity.toEntity
import woowacourse.shopping.data.entity.toRecentProduct
import woowacourse.shopping.data.util.toLocalDateTime
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val localDataSource: RecentProductLocalDataSource,
    private val productRepository: ProductRepository,
) : RecentProductRepository {
    override suspend fun getLastViewedProduct(): Result<RecentProduct?> =
        withContext(Dispatchers.IO) {
            val entity = localDataSource.getLastViewedProduct()
            if (entity != null) {
                val result = productRepository.getProductById(entity.productId)

                result.mapCatching { product ->
                    product?.let {
                        entity.toRecentProduct(it)
                    }
                }
            } else {
                Result.success(null)
            }
        }

    override suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProduct>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val entities = localDataSource.getPagedProducts(limit, offset)
                val productIds = entities.map { it.productId }

                val productResult = productRepository.getProductsByIds(productIds)
                val products = productResult.getOrElse { throw it } ?: emptyList()

                val productMap = products.associateBy { it.id }

                val recentProducts =
                    entities.mapNotNull { entity ->
                        productMap[entity.productId]?.let { product ->
                            RecentProduct(product, entity.viewedAt.toLocalDateTime())
                        }
                    }

                Result.success(recentProducts)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun replaceRecentProduct(recentProduct: RecentProduct): Result<Unit> =
        withContext(Dispatchers.IO) {
            localDataSource.replaceRecentProduct(recentProduct.toEntity())
            Result.success(Unit)
        }
}
