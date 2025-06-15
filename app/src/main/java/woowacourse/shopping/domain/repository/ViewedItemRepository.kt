package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ViewedItemRepository {
    suspend fun insertViewedItem(product: Product): Result<Unit>

    suspend fun getViewedItems(): Result<List<Product>?>

    suspend fun getLastViewedItem(): Result<Product?>
}
