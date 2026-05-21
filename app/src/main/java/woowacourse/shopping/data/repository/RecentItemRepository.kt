package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.entity.RecentItemEntity

class RecentItemRepository(
    private val recentItemDao: RecentItemDao,
) {
    suspend fun addRecentItemId(productId: Long) {
        recentItemDao.insert(RecentItemEntity(id = productId, timestamp = System.currentTimeMillis()))
        recentItemDao.deleteItemsExceedingLimit(MAX_RECENT_ITEMS_LIMIT)
    }

    fun getRecentItemIds(): Flow<List<Long>> =
        recentItemDao.getRecentItems(MAX_RECENT_ITEMS_LIMIT).map { entities ->
            entities.map { it.id }
        }

    suspend fun getLastViewedItemId(): Long? = recentItemDao.getLastViewedItem()?.id

    companion object {
        private const val MAX_RECENT_ITEMS_LIMIT = 10
    }
}
