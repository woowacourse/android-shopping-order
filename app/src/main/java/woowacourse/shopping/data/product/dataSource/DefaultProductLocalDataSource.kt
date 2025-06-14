package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.local.dao.RecentWatchingDao
import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity

class DefaultProductLocalDataSource(
    private val recentWatchingDao: RecentWatchingDao,
) : ProductLocalDataSource {
    override suspend fun insertRecentWatching(recentWatchingEntity: RecentWatchingEntity) {
        recentWatchingDao.insertRecentWatching(recentWatchingEntity)
    }

    override suspend fun getRecentWatchingProducts(size: Int): List<RecentWatchingEntity> =
        recentWatchingDao.getRecentWatchingProducts(size)
}
