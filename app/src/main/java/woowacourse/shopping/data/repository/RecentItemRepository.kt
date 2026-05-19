package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.model.Product

interface RecentItemRepository {
    suspend fun addRecentItem(product: Product)

    fun getRecentItems(): Flow<List<Product>>

    suspend fun getLastViewedItem(): Product?
}
