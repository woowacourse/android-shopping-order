package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentItem

interface RecentItemRepository {
    suspend fun addRecentItem(product: Product)

    fun getRecentItems(): Flow<List<RecentItem>>

    suspend fun getLastViewedItemId(): Long?
}
