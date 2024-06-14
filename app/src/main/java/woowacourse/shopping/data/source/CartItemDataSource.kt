package woowacourse.shopping.data.source

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse

interface CartItemDataSource {
    suspend fun loadCartItems(): Response<CartItemResponse>

    suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Response<CartItemResponse>

    suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Response<Unit>

    suspend fun deleteCartItem(id: Long): Response<Unit>

    suspend fun updateCartItem(
        id: Long,
        quantity: Int,
    ): Response<Unit>

    suspend fun loadCartItemCount(): Response<CartItemQuantityDto>
}
