package woowacourse.shopping.domain.repository.product

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.model.CartItem

// TODO: Cart 에 사용하는 기능 CartRepository로 분리
interface ProductRepository {
    fun loadAllProducts(page: Int): List<Product>

    fun loadPagedCartItem(): List<CartItem>

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

    fun addShoppingCartProduct(
        id: Long,
        quantity: Int,
    )

    fun removeShoppingCartProduct(id: Long)
}
