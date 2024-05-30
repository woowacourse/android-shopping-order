package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.CartItemDto

interface ShoppingProductsRepository {
    fun loadAllProducts(page: Int): List<Product>

    fun loadPagedCartItem(): List<CartItemDto>

    fun loadProduct(id: Long): Product

    fun isFinalPage(page: Int): Boolean

    fun isCartFinalPage(page: Int): Boolean

    fun shoppingCartProductQuantity(): Int

    fun increaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    )

    fun decreaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    )

    fun addShoppingCartProduct(id: Long)

    fun removeShoppingCartProduct(id: Long)
}
