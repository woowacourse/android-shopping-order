package woowacourse.shopping.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.toProduct
import woowacourse.shopping.data.entity.toEntity
import woowacourse.shopping.data.util.toLocalDateTime
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val localDataSource: RecentProductLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource,
) : RecentProductRepository {
    override suspend fun getLastViewedProduct(): Result<RecentProduct?> =
        localDataSource.getLastViewedProduct().mapCatching { entity ->
            if (entity != null) {
                val product = getProductById(entity.productId)
                RecentProduct(product, entity.viewedAt.toLocalDateTime())
            } else {
                null
            }
        }

    override suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProduct>> =
        localDataSource.getPagedProducts(limit, offset).mapCatching { entities ->
            coroutineScope {
                val productResults =
                    entities
                        .map { entity ->
                            async { entity to getProductById(entity.productId) }
                        }.awaitAll()

                productResults.map { (entity, product) ->
                    RecentProduct(product, entity.viewedAt.toLocalDateTime())
                }
            }
        }

    override suspend fun replaceRecentProduct(recentProduct: RecentProduct): Result<Unit> =
        localDataSource.replaceRecentProduct(recentProduct.toEntity())

    private suspend fun getProductById(id: Int): Product = productRemoteDataSource.getProductById(id).getOrThrow().toProduct()
}
