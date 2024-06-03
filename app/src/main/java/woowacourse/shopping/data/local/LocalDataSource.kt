package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.entity.RecentProductEntity

interface LocalDataSource {
    fun findByLimit(limit: Int): List<RecentProductEntity>

    fun findOne(): RecentProductEntity?

    fun saveRecentProduct(recentProductEntity: RecentProductEntity): Long
}
