package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.util.runCatchingDebugLog

class RecentProductLocalDataSourceImpl(
    private val recentProductDao: RecentProductDao,
) : RecentProductLocalDataSource {
    override suspend fun getRecentProducts(limit: Int): Result<List<RecentProductEntity>> =
        runCatchingDebugLog { recentProductDao.getRecentProducts(limit) }

    override suspend fun getRecentViewedProductCategory(): Result<String?> =
        runCatchingDebugLog { recentProductDao.getRecentViewedProductCategory() }

    override suspend fun insertRecentProduct(recentProduct: RecentProductEntity): Result<Unit> =
        runCatchingDebugLog { recentProductDao.insertRecentProduct(recentProduct) }

    override suspend fun trimToLimit(limit: Int): Result<Unit> =
        runCatchingDebugLog {
            val savedRecentProductCount = recentProductDao.getRecentProductCount()
            if (savedRecentProductCount > limit) {
                val overflowCount = savedRecentProductCount - limit
                recentProductDao.deleteOldestRecentProducts(overflowCount)
            }
        }
}
