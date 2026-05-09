package woowacourse.shopping.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.ShoppingItem

class MemoryShoppingItemRepository(
    shoppingItems: List<ShoppingItem>,
) : ShoppingItemRepository {
    private val shoppingItemByProductId: MutableMap<Long, ShoppingItem> =
        shoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }.toMutableMap()

    private val _shoppingItems = MutableStateFlow(createSnapshot())
    override val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems

    override fun getShoppingItemOrNull(productId: Long): ShoppingItem? =
        shoppingItemByProductId[productId]

    override fun getQuantity(productId: Long): Int = getShoppingItem(productId).getQuantity()

    override fun plusQuantity(productId: Long) {
        getShoppingItem(productId).plusQuantity()
        syncShoppingItems()
    }

    override fun minusQuantity(productId: Long) {
        val shoppingItem = getShoppingItem(productId)
        if (shoppingItem.getQuantity() == 0) {
            return
        }
        shoppingItem.minusQuantity()
        syncShoppingItems()
    }

    private fun getShoppingItem(productId: Long): ShoppingItem =
        shoppingItemByProductId[productId]
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")

    private fun syncShoppingItems() {
        _shoppingItems.value = createSnapshot()
    }

    private fun createSnapshot(): List<ShoppingItem> =
        shoppingItemByProductId.values.map { shoppingItem ->
            ShoppingItem(
                product = shoppingItem.getProduct(),
                quantity = shoppingItem.getQuantity(),
            )
        }
}
