package woowacourse.shopping.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.ShoppingItem

class MemoryShoppingItemRepository(
    shoppingItems: List<ShoppingItem>,
) : ShoppingItemRepository {
    private val items: MutableList<ShoppingItem> =
        shoppingItems.map { item -> item.copyItem() }.toMutableList()
    private val _shoppingItems = MutableStateFlow(emptyList<ShoppingItem>())
    override val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems

    init {
        publishSnapshot()
    }

    override fun getQuantity(productId: Long): Int = findItem(productId)?.getQuantity() ?: 0

    override fun plusQuantity(productId: Long) {
        val item = findItem(productId) ?: return
        item.plusQuantity()
        publishSnapshot()
    }

    override fun minusQuantity(productId: Long) {
        val item = findItem(productId) ?: return
        if (item.getQuantity() == 0) {
            return
        }
        item.minusQuantity()
        publishSnapshot()
    }

    private fun findItem(productId: Long): ShoppingItem? =
        items.find { item -> item.getProductId() == productId }

    private fun publishSnapshot() {
        _shoppingItems.value = items.map { item -> item.copyItem() }
    }

    private fun ShoppingItem.copyItem(): ShoppingItem =
        ShoppingItem(
            product = getProduct(),
            quantity = getQuantity(),
        )
}
