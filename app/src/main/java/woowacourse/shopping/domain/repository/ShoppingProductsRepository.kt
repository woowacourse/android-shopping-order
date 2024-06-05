package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.model.CartItem

interface ShoppingProductsRepository {
    fun pagedProducts(page: Int): List<Product>

    fun allProductsUntilPage(page: Int): List<Product>

    fun loadPagedCartItem(): List<CartItem>

    fun loadProduct(id: Long): Product

    fun isFinalPage(page: Int): Boolean

    fun shoppingCartProductQuantity(): Int

    fun increaseInShoppingCart(
        cartItemId: Long,
        quantity: Int,
    )

    fun decreaseInShoppingCart(
        cartItemId: Long,
        quantity: Int
    )

    fun increaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    )

    fun decreaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    )

    fun addShoppingCartProduct(
        id: Long,
        quantity: Int,
    )

    fun removeShoppingCartProduct(id: Long)
}
