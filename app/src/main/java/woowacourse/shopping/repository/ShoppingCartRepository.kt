package woowacourse.shopping.repository

import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem

interface ShoppingCartRepository {
    suspend fun add(shoppingItem: ShoppingItem)

    suspend fun remove(shoppingCartItem: ShoppingCartItem)

    suspend fun getShoppingItems(): List<ShoppingCartItem>

    suspend fun containsProduct(productId: Long): Boolean =
        getShoppingItems().any { shoppingCartItem -> shoppingCartItem.product.id == productId }

    suspend fun removeByProductId(productId: Long): Boolean {
        val targetItem =
            getShoppingItems().find { shoppingCartItem -> shoppingCartItem.product.id == productId }
                ?: return false
        remove(targetItem)
        return true
    }
}
