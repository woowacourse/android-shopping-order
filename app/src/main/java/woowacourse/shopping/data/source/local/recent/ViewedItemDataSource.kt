package woowacourse.shopping.data.source.local.recent

import woowacourse.shopping.data.model.ViewedItem

interface ViewedItemDataSource {
    suspend fun insertViewedItem(item: ViewedItem)

    suspend fun getRecentViewedItems(): List<ViewedItem>

    suspend fun getLastViewedItem(): ViewedItem?
}
