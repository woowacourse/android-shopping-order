package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.dto.CartItemDto
import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse

interface CartDataSource {
    fun getCartResponse(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse>

    fun addCartItem(cartItemRequest: CartItemRequest): Result<Unit>

    fun deleteCartItem(productId: Int): Result<Unit>

    fun updateCartItem(
        productId: Int,
        cartQuantityDto: CartQuantityDto,
    ): Result<Unit>

    fun getCartTotalQuantity(): Result<Int>
}
