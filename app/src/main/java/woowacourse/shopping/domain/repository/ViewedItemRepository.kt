package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ViewedItemRepository {
    suspend fun insertViewedItem(product: Product)

    suspend fun getViewedItems(): List<Product>

    suspend fun getLastViewedItem(): Product?
}
