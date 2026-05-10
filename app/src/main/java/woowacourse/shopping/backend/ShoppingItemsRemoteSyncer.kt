package woowacourse.shopping.backend

import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemEntity
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao

class ShoppingItemsRemoteSyncer(
    private val shoppingItemDao: ShoppingItemDao,
    private val productBackendDataSource: ProductBackendDataSource,
) {
    suspend fun sync() {
        val serverShoppingItems =
            productBackendDataSource
                .fetchProducts()
        val mergedShoppingItemEntities =
            serverShoppingItems.map { shoppingItem ->
                val currentQuantity =
                    shoppingItemDao.getQuantityOrNull(shoppingItem.getProductId()) ?: 0
                shoppingItem.toEntityWithQuantity(currentQuantity)
            }

        if (mergedShoppingItemEntities.isEmpty()) {
            shoppingItemDao.deleteAll()
            return
        }

        shoppingItemDao.insertAll(mergedShoppingItemEntities)
        shoppingItemDao.deleteByProductIdsNotIn(mergedShoppingItemEntities.map { entity -> entity.productId })
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
