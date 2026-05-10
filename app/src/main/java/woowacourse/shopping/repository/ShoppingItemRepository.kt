package woowacourse.shopping.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.ShoppingItem

interface ShoppingItemRepository {
    val shoppingItems: StateFlow<List<ShoppingItem>>

    fun getShoppingItemOrNull(productId: Long): ShoppingItem?

    fun getQuantity(productId: Long): Int

    fun plusQuantity(productId: Long, amount: Int = 1)

    fun minusQuantity(productId: Long, amount: Int = 1)
}
