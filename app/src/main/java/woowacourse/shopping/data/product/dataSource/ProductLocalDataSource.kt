package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity

interface ProductLocalDataSource {
    suspend fun insertRecentWatching(recentWatchingEntity: RecentWatchingEntity)

    suspend fun getRecentRecommendWatchingProducts(size: Int): List<RecentWatchingEntity>

    suspend fun getRecentWatchingProducts(size: Int): List<RecentWatchingEntity>
}
