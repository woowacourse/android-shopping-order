package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.domain.model.ShoppingCartItem

interface ShoppingCartRepository {
    fun observeShoppingItems(): Flow<List<ShoppingCartItem>>

    suspend fun requestCartItems(
        page: Int = 0,
        size: Int = 20,
        sort: List<String>? = null,
        force: Boolean = false,
    )

    suspend fun addOrIncreaseByProductId(
        productId: Long,
        amount: Int = 1,
    )

    suspend fun decreaseByProductId(productId: Long)

    suspend fun removeByProductId(productId: Long)
}
