package woowacourse.shopping.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.ShoppingItem

class MemoryShoppingItemRepository(
    shoppingItems: List<ShoppingItem>,
) : ShoppingItemRepository {
    private val items: MutableList<ShoppingItem> = shoppingItems.toMutableList()

    private val _shoppingItems = MutableStateFlow(createSnapshot())
    override val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems

    override fun getShoppingItemOrNull(productId: Long): ShoppingItem? =
        findShoppingItem(productId)

    override fun getQuantity(productId: Long): Int = findRequiredShoppingItem(productId).getQuantity()

    override fun plusQuantity(productId: Long, amount: Int) {
        findRequiredShoppingItem(productId).plusQuantity(amount)
        syncShoppingItems()
    }

    override fun minusQuantity(productId: Long) {
        val shoppingItem = findRequiredShoppingItem(productId)
        if (shoppingItem.getQuantity() == 0) {
            return
        }
        shoppingItem.minusQuantity()
        syncShoppingItems()
    }

    private fun findRequiredShoppingItem(productId: Long): ShoppingItem =
        findShoppingItem(productId)
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")

    private fun findShoppingItem(productId: Long): ShoppingItem? =
        items.find { shoppingItem -> shoppingItem.getProductId() == productId }

    private fun syncShoppingItems() {
        _shoppingItems.value = createSnapshot()
    }

    private fun createSnapshot(): List<ShoppingItem> =
        items.map { shoppingItem ->
            ShoppingItem(
                product = shoppingItem.getProduct(),
                quantity = shoppingItem.getQuantity(),
            )
        }
}
