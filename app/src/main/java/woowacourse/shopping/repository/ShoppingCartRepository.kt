package woowacourse.shopping.repository

import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem

interface ShoppingCartRepository {
    fun add(shoppingItem: ShoppingItem)

    fun remove(shoppingCartItem: ShoppingCartItem)

    fun getShoppingItems(): List<ShoppingCartItem>
}
