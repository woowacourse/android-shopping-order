package woowacourse.shopping.backend

import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemEntity

class ShoppingItemsRemoteSyncer(
    private val shoppingItemDao: ShoppingItemDao,
    private val productBackendDataSource: ProductBackendDataSource,
) {
    suspend fun sync() {
        val syncedItems =
            productBackendDataSource.fetchProducts().map { shoppingItem ->
                val preservedQuantity = shoppingItemDao.getQuantityOrNull(shoppingItem.getProductId()) ?: 0
                shoppingItem.toEntityWithQuantity(preservedQuantity)
            }

        if (syncedItems.isEmpty()) {
            shoppingItemDao.deleteAll()
            return
        }

        shoppingItemDao.insertAll(syncedItems)
        shoppingItemDao.deleteByProductIdsNotIn(syncedItems.map { entity -> entity.productId })
    }

    private fun ShoppingItem.toEntityWithQuantity(quantity: Int): ShoppingItemEntity =
        ShoppingItemEntity(
            productId = getProductId(),
            title = getProduct().getTitle(),
            price = getProduct().getPrice(),
            imageUrl = getProduct().imageUrl,
            quantity = quantity,
        )
}
