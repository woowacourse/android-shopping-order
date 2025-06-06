package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.local.recent.ViewedItemDao
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.mapper.toUiModel
import woowacourse.shopping.mapper.toViewedItem
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class ViewedItemRepositoryImpl(
    private val dao: ViewedItemDao,
) : ViewedItemRepository {
    override suspend fun insertViewedItem(product: ProductUiModel) {
        dao.insertViewedProduct(product.toViewedItem())
    }

    override suspend fun getViewedItems(): List<ProductUiModel> {
        val recentItems = dao.getRecentViewedItems()
        return recentItems.map { it.toUiModel() }
    }

    override suspend fun getLastViewedItem(): ProductUiModel? {
        val lastViewed = dao.getLastViewedItem()
        return lastViewed?.toUiModel()
    }
}
