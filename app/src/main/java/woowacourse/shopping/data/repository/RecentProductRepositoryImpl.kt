package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
    private val recentProductLimit: Int = 10,
) : RecentProductRepository {
    override fun getRecentProducts(
        limit: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        recentProductLocalDataSource
            .getRecentProducts(limit)
            .mapNotNull {
                productRemoteDataSource.fetchProduct(it.productId).getOrNull()?.toDomain()
            }
    }

    override fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        recentProductLocalDataSource.insertRecentProduct(RecentProductEntity(productId, category))
        recentProductLocalDataSource.trimToLimit(recentProductLimit)
    }
}
