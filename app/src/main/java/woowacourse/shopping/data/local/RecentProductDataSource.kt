package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.entity.RecentProductEntity

interface RecentProductDataSource {
    suspend fun findByLimit(limit: Int): List<RecentProductEntity>

    suspend fun findOne(): RecentProductEntity?

    suspend fun saveRecentProduct(recentProductEntity: RecentProductEntity): Long
}
