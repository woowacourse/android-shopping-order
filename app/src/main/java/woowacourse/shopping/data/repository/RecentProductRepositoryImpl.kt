package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.util.toLocalDateTime
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.toEntity
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val localDataSource: RecentProductLocalDataSource,
    private val productRepository: ProductRepository,
) : RecentProductRepository {
    override suspend fun getLastViewedProduct(): Result<RecentProduct?> {
        val entity = localDataSource.getLastViewedProduct()
        return if (entity != null) {
            val result = productRepository.getProductById(entity.productId)

            result.mapCatching { product ->
                product?.let {
                    RecentProduct(it, entity.viewedAt.toLocalDateTime())
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
        runCatching {
            val entities = localDataSource.getPagedProducts(limit, offset)
            val productIds = entities.map { it.productId }

            val productResult = productRepository.getProductsByIds(productIds)
            val products = productResult.getOrElse { throw it } ?: emptyList()

            val productMap = products.associateBy { it.id }

            entities.mapNotNull { entity ->
                productMap[entity.productId]?.let { product ->
                    RecentProduct(product, entity.viewedAt.toLocalDateTime())
                }
            }
        }

    override suspend fun replaceRecentProduct(recentProduct: RecentProduct): Result<Unit> {
        localDataSource.replaceRecentProduct(recentProduct.toEntity())
        return Result.success(Unit)
    }
}
