package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.recentproduct.RecentProduct
import woowacourse.shopping.data.recentproduct.RecentProductDao
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException
import kotlin.concurrent.thread

class RecentProductLocalDataSourceImpl(private val dao: RecentProductDao) :
    RecentProductLocalDataSource {
    override fun insert(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            var id = -1L
            thread {
                id = dao.insert(recentProduct)
            }.join()
            id
        }

    override fun findMostRecentProduct(): Result<RecentProduct> =
        runCatching {
            var recentProduct: RecentProduct? = null
            thread {
                recentProduct = dao.findMostRecentProduct()
            }.join()
            recentProduct ?: throw ShoppingException(ShoppingError.RecentProductNotFound)
        }

    override fun findAll(): Result<List<RecentProduct>> =
        runCatching {
            var recentProducts = emptyList<RecentProduct>()
            thread {
                recentProducts = dao.findAll()
            }.join()
            recentProducts
        }

    override fun deleteAll(): Result<Unit> =
        runCatching {
            thread {
                dao.deleteAll()
            }.join()
        }
}
