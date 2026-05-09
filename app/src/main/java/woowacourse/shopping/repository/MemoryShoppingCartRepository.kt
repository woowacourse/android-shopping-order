package woowacourse.shopping.repository

import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem

class MemoryShoppingCartRepository: ShoppingCartRepository {

    private val items: MutableList<ShoppingCartItem> = mutableListOf()
    private var nextShoppingCartItemId: Long = 0L

    override fun add(shoppingItem: ShoppingItem) {
        val alreadyInCart =
            items.any { shoppingCartItem -> shoppingCartItem.product.id == shoppingItem.getProductId() }
        if (alreadyInCart) {
            return
        }
        items.add(
            ShoppingCartItem(
                id = nextShoppingCartItemId++,
                shoppingItem = shoppingItem,
            )
        )
    }

    override fun remove(shoppingCartItem: ShoppingCartItem) {
        val removed = items.remove(shoppingCartItem)
        if (!removed) {
            throw IllegalArgumentException("장바구니에 존재하지 않는 상품입니다")
        }
    }

    override fun getShoppingItems(): List<ShoppingCartItem> = items
}
