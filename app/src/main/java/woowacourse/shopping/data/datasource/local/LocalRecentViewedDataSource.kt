package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.local.entity.RecentProductEntity

interface LocalRecentViewedDataSource {
    suspend fun loadAll(): List<RecentProductEntity>

    suspend fun save(recentProduct: RecentProductEntity)

    suspend fun getMostRecent(): RecentProductEntity?
}
