package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.data.source.local.recent.ViewedItemDao
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ViewedItemRepository

class ViewedItemRepositoryImpl(
    private val dao: ViewedItemDao,
) : ViewedItemRepository {
    override suspend fun insertViewedItem(product: Product): Result<Unit> =
        runCatching {
            dao.insertViewedProduct(product.toEntity())
        }

    override suspend fun getViewedItems(): Result<List<Product>?> =
        runCatching {
            val items = dao.getRecentViewedItems(RECENT_VIEWED_ITEMS_COUNT)
            items?.map { it.toDomain() }
        }

    override suspend fun getLastViewedItem(): Result<Product?> =
        runCatching {
            dao.getRecentViewedItems(LAST_VIEWED_ITEM_COUNT)
                ?.firstOrNull()
                ?.toDomain()
        }

    private fun Product.toEntity() =
        ViewedItem(
            id = this.id,
            imageUrl = this.imageUrl,
            name = this.name,
            price = this.price,
            category = this.category,
        )

    private fun ViewedItem.toDomain() =
        Product(
            id = this.id,
            name = this.name,
            price = this.price,
            imageUrl = this.imageUrl,
            category = this.category,
        )

    companion object {
        private const val RECENT_VIEWED_ITEMS_COUNT = 10
        private const val LAST_VIEWED_ITEM_COUNT = 1
    }
}
