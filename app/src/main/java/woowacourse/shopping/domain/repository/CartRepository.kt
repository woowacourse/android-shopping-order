package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Products

interface CartRepository {
    suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Products

    suspend fun fetchAllCartProducts(): Products

    suspend fun fetchCartItemCount(): Int

    suspend fun addCartProduct(
        productId: Long,
        quantity: Int,
    ): Unit

    suspend fun deleteCartProduct(cartId: Long): Unit

    suspend fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    ): Unit
}
