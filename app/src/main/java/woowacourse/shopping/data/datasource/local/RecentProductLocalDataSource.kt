package woowacourse.shopping.data.datasource.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dao.RecentProductDao
import woowacourse.shopping.data.entity.RecentProductEntity

class RecentProductLocalDataSource(
    private val dao: RecentProductDao,
) {
    suspend fun getLastViewedProduct(): Result<RecentProductEntity?> =
        withContext(Dispatchers.IO) {
            runCatching {
                dao.getLastViewedProduct()
            }
        }

    suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProductEntity>> =
        withContext(Dispatchers.IO) {
            runCatching {
                dao.getPagedProducts(limit, offset)
            }
        }

    suspend fun replaceRecentProduct(recentProductEntity: RecentProductEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                dao.replaceRecentProduct(recentProductEntity)
            }
        }
}
