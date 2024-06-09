package woowacourse.shopping.domain.repository

import woowacourse.shopping.ui.model.CartItem

interface ShoppingCartRepository {
    suspend fun loadAllCartItems(): Result<List<CartItem>>

    suspend fun shoppingCartProductQuantity(): Result<Int>

    suspend fun updateProductQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun addShoppingCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Unit>


    suspend fun removeShoppingCartProduct(cartItemId: Long): Result<Unit>

    suspend fun findCartItemByProductId(productId: Long): Result<CartItem>
}
