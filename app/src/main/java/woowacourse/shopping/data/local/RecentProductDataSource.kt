package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.entity.RecentProductEntity

interface RecentProductDataSource {
    suspend fun findAllByLimit(limit: Int): List<RecentProductEntity>

    suspend fun findOrNull(): RecentProductEntity?

    suspend fun save(recentProductEntity: RecentProductEntity): Long
}
