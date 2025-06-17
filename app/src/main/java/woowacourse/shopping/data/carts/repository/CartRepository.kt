package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.api.ApiResult
import woowacourse.shopping.domain.model.Goods

interface CartRepository {
    suspend fun fetchAllCartItems(): ApiResult<CartResponse>

    suspend fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
    ): ApiResult<CartResponse>

    suspend fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): ApiResult<Int>

    suspend fun delete(cartId: Int): ApiResult<Int>

    suspend fun addCartItem(
        goods: Goods,
        quantity: Int,
    ): ApiResult<AddItemResult>
}
