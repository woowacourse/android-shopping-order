package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.dao.RecentProductDao
import woowacourse.shopping.data.entity.RecentProductEntity

class RecentProductLocalDataSource(
    private val dao: RecentProductDao,
) {
    suspend fun getLastViewedProduct(): Result<RecentProductEntity?> =
        runCatching {
            dao.getLastViewedProduct()
        }

    suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProductEntity>> =
        runCatching {
            dao.getPagedProducts(limit, offset)
        }

    suspend fun replaceRecentProduct(recentProductEntity: RecentProductEntity): Result<Unit> =
        runCatching {
            dao.replaceRecentProduct(recentProductEntity)
        }
}
