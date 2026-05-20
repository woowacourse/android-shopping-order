package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.mapper.toEntity
import woowacourse.shopping.model.Product

class RecentItemRepository(
    private val recentItemDao: RecentItemDao,
) {
    suspend fun addRecentItem(product: Product) {
        recentItemDao.upsert(product.toEntity(System.currentTimeMillis()))
        recentItemDao.deleteOldItem()
    }

    fun getRecentItems(): Flow<List<String>> =
        recentItemDao.getRecentItems().map { list ->
            list.map { it.id }
        }

    suspend fun getLastViewedItem(): String? = recentItemDao.getLastViewedItem()?.id
}
