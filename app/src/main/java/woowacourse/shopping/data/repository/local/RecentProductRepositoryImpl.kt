package woowacourse.shopping.data.repository.local

import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.data.entity.toData
import woowacourse.shopping.data.entity.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {
    override suspend fun getRecentProducts(): Result<List<Product>> =
        runCatching {
            recentProductLocalDataSource
                .getProducts()
                .map { recentProduct -> recentProduct.toDomain() }
        }

    override suspend fun getMostRecentProduct(): Result<Product?> =
        runCatching {
            recentProductLocalDataSource.getMostRecentProduct()?.toDomain()
        }

    override suspend fun insertRecentProduct(product: Product): Result<Unit> =
        runCatching {
            val recentProducts = recentProductLocalDataSource.getProducts()
            val productId = product.productId

            if (isNewProduct(recentProducts, productId) && recentProducts.size == 10) {
                val oldProduct = recentProductLocalDataSource.getOldestProduct()
                recentProductLocalDataSource.delete(oldProduct)
            }

            recentProductLocalDataSource.insert(product.toData())
        }

    private fun isNewProduct(
        recentProducts: List<RecentlyViewedProduct>,
        productId: Long,
    ): Boolean = recentProducts.none { it.productId == productId }
}
