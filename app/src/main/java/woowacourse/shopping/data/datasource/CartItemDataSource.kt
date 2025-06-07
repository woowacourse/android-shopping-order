package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemDataSource {
    suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<CartItemResponse>

    suspend fun submitCartItem(cartItem: CartItemRequest): Result<Long>

    suspend fun removeCartItem(cartId: Long): Result<Unit>

    suspend fun updateCartItem(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit>

    suspend fun fetchCartItemsCount(): Result<Quantity>
}
