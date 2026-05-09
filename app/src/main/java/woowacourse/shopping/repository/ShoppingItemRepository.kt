package woowacourse.shopping.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.ShoppingItem

interface ShoppingItemRepository {
    val shoppingItems: StateFlow<List<ShoppingItem>>

    fun getQuantity(productId: Long): Int

    fun plusQuantity(productId: Long)

    fun minusQuantity(productId: Long)
}
