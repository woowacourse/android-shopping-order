package woowacourse.shopping.data.recent.local

import woowacourse.shopping.data.recent.local.dao.RecentProductDao
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RoomRecentProductRepository(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
    override suspend fun findLastOrNull(): Result<RecentProduct?> {
        return runCatching {
            recentProductDao.findRange(1).firstOrNull()?.toRecentProduct()
        }
    }

    override suspend fun findRecentProducts(): Result<List<RecentProduct>> {
        return runCatching {
            recentProductDao.findRange(FIND_RECENT_PRODUCTS_COUNT).toRecentProducts()
        }
    }

    override suspend fun save(product: Product): Result<Unit> {
        return runCatching {
            if (recentProductDao.findOrNullByProductId(product.id) == null) {
                recentProductDao.insert(
                    RecentProductEntity(
                        product = product.toProductEntity(),
                        seenDateTime = LocalDateTime.now(),
                    ),
                )
                return@runCatching
            }
            recentProductDao.update(product.id, LocalDateTime.now())
        }
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10

        @Volatile
        private var instance: RoomRecentProductRepository? = null

        fun getInstance(recentProductDao: RecentProductDao): RoomRecentProductRepository {
            return instance ?: synchronized(this) {
                RoomRecentProductRepository(recentProductDao).also { instance = it }
            }
        }
    }
}
