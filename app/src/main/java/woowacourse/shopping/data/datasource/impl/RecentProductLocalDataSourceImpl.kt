package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.exception.ShoppingError
import woowacourse.shopping.data.local.db.recentproduct.RecentProductDao
import woowacourse.shopping.data.model.RecentProduct

class RecentProductLocalDataSourceImpl(private val dao: RecentProductDao) :
    RecentProductLocalDataSource {
    override suspend fun insert(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            dao.insert(recentProduct)
        }

    override suspend fun findMostRecentProduct(): Result<RecentProduct> =
        runCatching {
            dao.findMostRecentProduct()
                ?: throw ShoppingError.RecentProductNotFound
        }

    override suspend fun findAll(): Result<List<RecentProduct>> =
        runCatching {
            dao.findAll()
        }

    override suspend fun deleteAll(): Result<Unit> =
        runCatching {
            dao.deleteAll()
        }
}
