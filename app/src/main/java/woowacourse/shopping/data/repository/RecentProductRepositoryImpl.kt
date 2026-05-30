package woowacourse.shopping.data.repository

import androidx.room.withTransaction
import woowacourse.shopping.data.local.room.ShoppingDatabase
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDao
import woowacourse.shopping.data.local.room.recentproduct.RecentProductEntity
import woowacourse.shopping.domain.model.recentproduct.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val database: ShoppingDatabase,
    private val recentProductDao: RecentProductDao,
) : RecentProductRepository {
    companion object {
        private const val MAX_RECENT_PRODUCTS = 10
    }

    override suspend fun recordView(productId: Long) {
        database.withTransaction {
            recentProductDao.upsert(
                RecentProductEntity.fromDomain(
                    RecentProduct(
                        productId = productId,
                        viewedAtMillis = System.currentTimeMillis(),
                    ),
                ),
            )
            recentProductDao.trimTo(MAX_RECENT_PRODUCTS)
        }
    }

    override suspend fun getRecentProducts(limit: Int): List<RecentProduct> =
        recentProductDao
            .getRecentProducts(limit.coerceAtLeast(0))
            .map(RecentProductEntity::toDomain)

    override suspend fun getLatestViewedProductExcluding(productId: Long): RecentProduct? =
        recentProductDao
            .getLatestViewedProductExcluding(productId)
            ?.toDomain()
}
