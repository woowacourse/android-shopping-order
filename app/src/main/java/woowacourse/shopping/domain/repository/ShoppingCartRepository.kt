package woowacourse.shopping.domain.repository

import woowacourse.shopping.ui.model.CartItem

interface ShoppingCartRepository {
    fun loadAllCartItems(): List<CartItem>

    fun shoppingCartProductQuantity(): Int

    fun updateProductQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    fun addShoppingCartProduct(
        productId: Long,
        quantity: Int,
    )

    fun removeShoppingCartProduct(cartItemId: Long)

    suspend fun loadAllCartItems2(): Result<List<CartItem>>

    suspend fun shoppingCartProductQuantity2(): Result<Int>

    suspend fun updateProductQuantity2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun addShoppingCartProduct2(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun removeShoppingCartProduct2(cartItemId: Long): Result<Unit>

    suspend fun findCartItemByProductId(productId: Long): Result<CartItem>
}
