package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartWithProduct

interface CartRepository {
    suspend fun getCartItem(productId: Long): CartWithProduct

    suspend fun getAllCartItems(): List<CartWithProduct>

    suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    )

    suspend fun deleteCartItem(id: Long)

    suspend fun getCartItemCounts(): Int

    suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    )

    suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    )
}
