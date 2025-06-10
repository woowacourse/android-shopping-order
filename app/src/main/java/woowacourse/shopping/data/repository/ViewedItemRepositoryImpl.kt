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
            val items = dao.getRecentViewedItems()
            items?.map { it.toDomain() }
        }

    override suspend fun getLastViewedItem(): Result<Product?> =
        runCatching {
            val item = dao.getLastViewedItem()
            item?.toDomain()
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
}
