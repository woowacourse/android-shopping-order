package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.cart.Cart
import woowacourse.shopping.domain.model.cart.CartWithProduct

interface CartRepository {
    suspend fun getCartItemByProductId(productId: Long): Result<CartWithProduct>

    suspend fun getCartItemByCartId(cartId: Long): Result<CartWithProduct>

    suspend fun getAllCartItems(): Result<List<Cart>>

    suspend fun getAllCartItemsWithProduct(): Result<List<CartWithProduct>>

    suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Long): Result<Unit>

    suspend fun getCartItemCounts(): Result<Int>

    suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun order(cartItemIds: List<Long>)
}
