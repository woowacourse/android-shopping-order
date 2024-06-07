package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse

interface CartDataSource {
    suspend fun getCartResponse(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse>

    suspend fun addCartItem(cartItemRequest: CartItemRequest): Result<Int>

    suspend fun deleteCartItem(productId: Int): Result<Unit>

    suspend fun updateCartItem(
        productId: Int,
        cartQuantityDto: CartQuantityDto,
    ): Result<Unit>

    suspend fun getCartTotalQuantity(): Result<Int>
}
