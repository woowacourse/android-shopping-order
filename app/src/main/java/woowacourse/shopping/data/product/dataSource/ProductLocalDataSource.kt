package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity

interface ProductLocalDataSource {
    fun insertRecentWatching(recentWatchingEntity: RecentWatchingEntity)

    fun getRecentRecommendWatchingProducts(size: Int): List<RecentWatchingEntity>

    fun getRecentWatchingProducts(size: Int): List<RecentWatchingEntity>
}
