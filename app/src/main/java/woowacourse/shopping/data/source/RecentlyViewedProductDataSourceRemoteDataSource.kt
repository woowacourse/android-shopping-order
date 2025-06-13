package woowacourse.shopping.data.source

import woowacourse.shopping.data.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import kotlin.concurrent.thread

class RecentlyViewedProductDataSourceRemoteDataSource(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
) : RecentlyViewedProductDataSource {
    override suspend fun insertRecentlyViewedProductUid(uid: Int) {
        recentlyViewedProductDao.insertRecentlyViewedProductUid(
            RecentlyViewedProductEntity(
                uid
            )
        )
    }

    override suspend fun getRecentlyViewedProducts(): List<Int> {
        val uids = recentlyViewedProductDao.getRecentlyViewedProductUids()
        return uids
    }

    override suspend fun getLatestViewedProduct(): Int {
        val uid = recentlyViewedProductDao.getLatestViewedProductUid()
        return uid
    }

    companion object {
        private var instance: RecentlyViewedProductDataSourceRemoteDataSource? = null

        @Synchronized
        fun initialize(recentlyViewedProductDao: RecentlyViewedProductDao): RecentlyViewedProductDataSourceRemoteDataSource =
            instance
                ?: RecentlyViewedProductDataSourceRemoteDataSource(recentlyViewedProductDao).also {
                    instance = it
                }
    }
}
