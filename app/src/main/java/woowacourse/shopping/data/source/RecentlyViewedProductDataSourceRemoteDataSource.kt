package woowacourse.shopping.data.source

import woowacourse.shopping.data.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import kotlin.concurrent.thread

class RecentlyViewedProductDataSourceRemoteDataSource(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
) : RecentlyViewedProductDataSource {
    override fun insertRecentlyViewedProductUid(uid: Int) {
        thread {
            recentlyViewedProductDao.insertRecentlyViewedProductUid(RecentlyViewedProductEntity(uid))
        }
    }

    override fun getRecentlyViewedProducts(callback: (List<Int>) -> Unit) {
        thread {
            val uids = recentlyViewedProductDao.getRecentlyViewedProductUids()
            callback(uids)
        }
    }

    override fun getLatestViewedProduct(callback: (Int) -> Unit) {
        thread {
            val uid = recentlyViewedProductDao.getLatestViewedProductUid()
            callback(uid)
        }
    }

    companion object {
        private var instance: RecentlyViewedProductDataSourceRemoteDataSource? = null

        @Synchronized
        fun initialize(recentlyViewedProductDao: RecentlyViewedProductDao): RecentlyViewedProductDataSourceRemoteDataSource =
            instance ?: RecentlyViewedProductDataSourceRemoteDataSource(recentlyViewedProductDao).also {
                instance = it
            }
    }
}
