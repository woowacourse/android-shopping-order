package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.domain.model.CartItem

interface CartItemDataSource {
    suspend fun loadCartItems(): Result<List<CartItem>>

    suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Result<CartItemResponse>

    suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun loadCartItemCount(): Result<CartItemQuantityDto>
}
