package woowacourse.shopping.data.datasource.local.cart

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.domain.model.ShoppingCartItem

interface ShoppingCartLocalDataSource {
    fun observeShoppingCartItems(): Flow<List<ShoppingCartItem>>

    suspend fun addIfAbsent(productId: Long)

    suspend fun removeByProductId(productId: Long)
}
