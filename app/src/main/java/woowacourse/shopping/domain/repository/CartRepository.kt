package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.response.Response

interface CartRepository {
    suspend fun cartItem(productId: Long): CartWithProduct

    suspend fun cartItemOrNull(productId: Long): CartWithProduct?

    suspend fun cartItemResponse(productId: Long): Response<CartWithProduct>

    suspend fun allCartItems(): List<CartWithProduct>

    suspend fun allCartItemsResponse(): Response<List<CartWithProduct>>

    suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Response<Unit>

    suspend fun deleteCartItem(id: Long): Response<Unit>

    suspend fun cartItemsCount(): Int

    suspend fun cartItemsCountOrNull(): Int?

    suspend fun cartItemsCountResponse(): Response<Int>

    suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Response<Unit>

    suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Response<Unit>
}
