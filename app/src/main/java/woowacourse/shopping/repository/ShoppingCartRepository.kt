package woowacourse.shopping.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.model.ShoppingCartItem

interface ShoppingCartRepository {
    fun observeShoppingItems(): Flow<List<ShoppingCartItem>>

    suspend fun addIfAbsent(productId: Long)

    suspend fun remove(shoppingCartItem: ShoppingCartItem)

    suspend fun getShoppingItems(): List<ShoppingCartItem>

    suspend fun removeByProductId(productId: Long): Boolean
}
