package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.domain.model.ShoppingCartItem

interface ShoppingCartRepository {
    fun observeShoppingItems(): Flow<List<ShoppingCartItem>>

    suspend fun addIfAbsent(productId: Long)

    suspend fun removeByProductId(productId: Long): Boolean
}
