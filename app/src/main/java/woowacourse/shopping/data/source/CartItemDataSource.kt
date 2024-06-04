package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemResult

interface CartItemDataSource {
    suspend fun loadCartItems(): Result<CartItemResponse>

    suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartItem>>

    suspend fun loadCartItemResult(
        productId: Long,
    ): Result<CartItemResult>

    suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun loadCartItemCount(): Result<Int>
}
