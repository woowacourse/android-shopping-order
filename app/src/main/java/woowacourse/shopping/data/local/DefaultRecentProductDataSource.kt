package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.RecentProductEntity

class DefaultRecentProductDataSource(private val recentProductDao: RecentProductDao) : RecentProductDataSource {
    override suspend fun findByLimit(limit: Int): List<RecentProductEntity> {
        return recentProductDao.findAllByLimit(limit)
    }

    override suspend fun findOne(): RecentProductEntity? {
        return recentProductDao.findOrNull()
    }

    override suspend fun saveRecentProduct(recentProductEntity: RecentProductEntity): Long {
        recentProductDao.save(recentProductEntity)
        return recentProductEntity.productId
    }
}
