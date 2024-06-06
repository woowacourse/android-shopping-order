package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDao
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RecentProductRepositoryImpl private constructor(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
    override suspend fun insert(productId: Long): Long =
        recentProductDao.insert(
            RecentProduct(
                productId = productId,
                recentTime = LocalDateTime.now(),
            ),
        )


    override suspend fun findMostRecentProduct(): RecentProduct? =
        recentProductDao.findMostRecentProduct()

    override suspend fun findAll(): List<RecentProduct> = recentProductDao.findAll()

    override suspend fun deleteAll() {
        recentProductDao.deleteAll()
    }

    companion object {
        private var instance: RecentProductRepository? = null

        fun get(recentProductDao: RecentProductDao): RecentProductRepository {
            return instance ?: synchronized(this) {
                instance ?: RecentProductRepositoryImpl(recentProductDao).also { instance = it }
            }
        }
    }
}
