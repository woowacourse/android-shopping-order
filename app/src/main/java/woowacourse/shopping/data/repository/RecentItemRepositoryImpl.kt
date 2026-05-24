package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.mapper.toDomain
import woowacourse.shopping.data.localdb.mapper.toRecentItemEntity
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentItem

class RecentItemRepositoryImpl(
    private val recentItemDao: RecentItemDao,
) : RecentItemRepository {
    override suspend fun addRecentItem(product: Product) {
        recentItemDao.insert(product.toRecentItemEntity(System.currentTimeMillis()))
        recentItemDao.deleteItemsExceedingLimit(MAX_RECENT_ITEMS_LIMIT)
    }

    override fun getRecentItems(): Flow<List<RecentItem>> =
        recentItemDao.getRecentItems(MAX_RECENT_ITEMS_LIMIT).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getLastViewedItemId(): Long? = recentItemDao.getLastViewedItem()?.productId

    companion object {
        private const val MAX_RECENT_ITEMS_LIMIT = 10
    }
}
