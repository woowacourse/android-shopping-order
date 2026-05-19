package woowacourse.shopping.mockup

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.entity.RecentItemEntity

class MockRecentItemDao : RecentItemDao {
    private val items = MutableStateFlow<List<RecentItemEntity>>(emptyList())
    var deleteOldItemCount = 0
        private set

    override suspend fun insert(item: RecentItemEntity) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }

    override fun getRecentItems(): Flow<List<RecentItemEntity>> =
        items.map { entities ->
            entities
                .sortedWith(compareByDescending<RecentItemEntity> { it.timestamp }.thenByDescending { it.id })
                .take(10)
        }

    override suspend fun getRecentItemById(id: String): RecentItemEntity? = items.value.firstOrNull { it.id == id }

    override suspend fun deleteOldItem() {
        deleteOldItemCount++
        val recentIds =
            items.value
                .sortedWith(compareByDescending<RecentItemEntity> { it.timestamp }.thenByDescending { it.id })
                .take(10)
                .map { it.id }
                .toSet()
        items.value = items.value.filter { it.id in recentIds }
    }

    override suspend fun getLastViewedItem(): RecentItemEntity? =
        items.value.maxWithOrNull(compareBy<RecentItemEntity> { it.timestamp }.thenBy { it.id })
}
