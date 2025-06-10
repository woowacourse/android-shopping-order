package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.local.recent.ViewedItemDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toViewedItem

class ViewedItemRepositoryImpl(
    private val viewedItemDataSource: ViewedItemDataSource,
) : ViewedItemRepository {
    override suspend fun insertViewedItem(product: Product) {
        viewedItemDataSource.insertViewedItem(product.toViewedItem())
    }

    override suspend fun getViewedItems(): Result<List<Product>> =
        runCatching {
            viewedItemDataSource.getRecentViewedItems().map { it.toDomain() }
        }

    override suspend fun getLastViewedItem(): Result<Product?> =
        runCatching {
            viewedItemDataSource.getLastViewedItem()?.toDomain()
        }
}
