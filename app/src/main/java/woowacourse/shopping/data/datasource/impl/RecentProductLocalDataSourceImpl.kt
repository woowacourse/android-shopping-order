package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.recentproduct.RecentProduct
import woowacourse.shopping.data.recentproduct.RecentProductDao
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException

class RecentProductLocalDataSourceImpl(private val dao: RecentProductDao) :
    RecentProductLocalDataSource {
    override suspend fun insert(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            dao.insert(recentProduct)
        }

    override suspend fun findMostRecentProduct(): Result<RecentProduct> =
        runCatching {
            dao.findMostRecentProduct()
                ?: throw ShoppingException(ShoppingError.RecentProductNotFound)
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
