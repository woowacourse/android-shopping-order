package woowacourse.shopping.data.repository.recentproduct

import androidx.room.Transaction
import woowacourse.shopping.data.local.recentproduct.RecentProductDao
import woowacourse.shopping.data.local.recentproduct.RecentProductEntity
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductDao: RecentProductDao,
) : RecentProductRepository {
    override suspend fun loadProducts(): List<Long> = recentProductDao.findAll(MAX_SIZE)

    @Transaction
    override suspend fun insert(id: Long) {
        recentProductDao.insert(
            RecentProductEntity(
                productId = id,
                viewedAt = System.currentTimeMillis(),
            ),
        )
        recentProductDao.trim(MAX_SIZE)
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}
