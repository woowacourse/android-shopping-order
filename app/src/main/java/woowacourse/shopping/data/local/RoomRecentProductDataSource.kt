package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.RecentProductEntity

class RoomRecentProductDataSource(private val recentProductDao: RecentProductDao) : RecentProductDataSource {
    override suspend fun findAllByLimit(limit: Int): List<RecentProductEntity> {
        return recentProductDao.findAllByLimit(limit)
    }

    override suspend fun findOrNull(): RecentProductEntity? {
        return recentProductDao.findOrNull()
    }

    override suspend fun save(recentProductEntity: RecentProductEntity): Long {
        recentProductDao.save(recentProductEntity)
        return recentProductEntity.productId
    }
}
