package woowacourse.shopping.data.recent.local

import woowacourse.shopping.data.recent.local.dao.RecentProductDao
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime
import kotlin.concurrent.thread

class RoomRecentProductRepository(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
    override fun findLastOrNull(): RecentProduct? {
        var lastRecentProductEntity: RecentProductEntity? = null
        thread {
            lastRecentProductEntity = recentProductDao.findRange(1).firstOrNull()
        }.join()
        return lastRecentProductEntity?.toRecentProduct()
    }

    override fun findRecentProducts(): List<RecentProduct> {
        var lastRecentProductEntity: List<RecentProductEntity> = emptyList()
        thread {
            lastRecentProductEntity = recentProductDao.findRange(FIND_RECENT_PRODUCTS_COUNT)
        }.join()
        return lastRecentProductEntity.toRecentProducts()
    }

    override fun save(product: Product) {
        thread {
            if (recentProductDao.findOrNullByProductId(product.id) == null) {
                recentProductDao.insert(
                    RecentProductEntity(
                        product = product.toProductEntity(),
                        seenDateTime = LocalDateTime.now(),
                    ),
                )
                return@thread
            }
            recentProductDao.update(product.id, LocalDateTime.now())
        }.join()
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
