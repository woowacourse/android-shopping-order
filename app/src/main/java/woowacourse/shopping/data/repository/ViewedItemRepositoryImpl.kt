package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.data.source.local.recent.ViewedItemDao
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toViewedItem

class ViewedItemRepositoryImpl(
    private val dao: ViewedItemDao,
) : ViewedItemRepository {
    override suspend fun insertViewedItem(product: Product) {
        dao.insertViewedProduct(product.toViewedItem())
    }

    override suspend fun getViewedItems(): List<Product> {
        val recentItems: List<ViewedItem> = dao.getRecentViewedItems()
        return recentItems.map { it.toDomain() }
    }

    override suspend fun getLastViewedItem(): Product? {
        val lastViewed: ViewedItem? = dao.getLastViewedItem()
        return lastViewed?.toDomain()
    }
}
