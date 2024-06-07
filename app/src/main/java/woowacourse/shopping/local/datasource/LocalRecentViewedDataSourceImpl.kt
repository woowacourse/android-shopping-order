package woowacourse.shopping.local.datasource

import woowacourse.shopping.data.datasource.local.LocalRecentViewedDataSource
import woowacourse.shopping.local.dao.RecentProductDao
import woowacourse.shopping.local.entity.RecentProductEntity

class LocalRecentViewedDataSourceImpl(private val dao: RecentProductDao) :
    LocalRecentViewedDataSource {
    override suspend fun loadAll(): List<RecentProductEntity> {
        return dao.loadAll()
    }

    override suspend fun save(recentProduct: RecentProductEntity): Long {
        return dao.insert(recentProduct)
    }

    override suspend fun getMostRecent(): RecentProductEntity? {
        return dao.getMostRecent()
    }
}
