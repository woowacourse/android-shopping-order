package woowacourse.shopping.data.recent.local

import woowacourse.shopping.data.recent.local.dao.RecentProductDao
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
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

    override fun save(productId: Int) {
        thread {
            if (recentProductDao.findOrNull(productId) == null) {
                recentProductDao.insert(RecentProductEntity(productId = productId, seenDateTime = LocalDateTime.now()))
                return@thread
            }
            recentProductDao.update(productId, LocalDateTime.now())
        }.join()
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
    }
}
