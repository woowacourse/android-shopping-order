package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.RecentProductEntity

class RoomDataSource(private val recentProductDao: RecentProductDao) : LocalDataSource {
    override fun findByLimit(limit: Int): List<RecentProductEntity> {
        return recentProductDao.findByLimit(limit)
    }

    override fun findOne(): RecentProductEntity? {
        return recentProductDao.findOne()
    }

    override fun saveRecentProduct(recentProductEntity: RecentProductEntity): Long {
        recentProductDao.saveRecentProduct(recentProductEntity)
        return recentProductEntity.productId
    }
}
