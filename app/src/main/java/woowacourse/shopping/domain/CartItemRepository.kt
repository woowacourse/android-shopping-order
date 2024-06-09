package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest

interface CartItemRepository {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>>

    suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int>

    suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun getCartItemsCounts(): Result<Int>
}
