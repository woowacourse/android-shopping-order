package woowacourse.shopping.data.source.local.recent

import woowacourse.shopping.data.model.ViewedItem

class ViewedItemLocalDataSource(
    private val dao: ViewedItemDao,
) : ViewedItemDataSource {
    override suspend fun insertViewedItem(item: ViewedItem) {
        dao.insertViewedProduct(item)
    }

    override suspend fun getRecentViewedItems(): List<ViewedItem> = dao.getRecentViewedItems()

    override suspend fun getLastViewedItem(): ViewedItem? = dao.getLastViewedItem()
}
