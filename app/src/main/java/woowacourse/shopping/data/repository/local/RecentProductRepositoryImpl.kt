package woowacourse.shopping.data.repository.local

import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.data.entity.toData
import woowacourse.shopping.data.entity.toDomain
import woowacourse.shopping.data.handleResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {
    override suspend fun getRecentProducts(): Result<List<Product>> =
        recentProductLocalDataSource
            .getProducts()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getMostRecentProduct(): Result<Product?> =
        recentProductLocalDataSource
            .getMostRecentProduct()
            .map { it?.toDomain() }

    override suspend fun insertRecentProduct(product: Product): Result<Unit> =
        handleResult {
            val productId = product.productId
            val recentProducts =
                recentProductLocalDataSource
                    .getProducts()
                    .getOrThrow()

            if (isNewProduct(recentProducts, productId) && recentProducts.size == 10) {
                val oldest =
                    recentProductLocalDataSource
                        .getOldestProduct()
                        .getOrThrow()

                recentProductLocalDataSource
                    .delete(oldest)
                    .getOrThrow()
            }

            return recentProductLocalDataSource.insert(product.toData())
        }

    private fun isNewProduct(
        recentProducts: List<RecentlyViewedProduct>,
        productId: Long,
    ): Boolean = recentProducts.none { it.productId == productId }
}
