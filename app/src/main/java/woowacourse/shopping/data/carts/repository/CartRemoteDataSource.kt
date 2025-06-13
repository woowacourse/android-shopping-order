package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.api.ApiResult

interface CartRemoteDataSource {
    suspend fun fetchCartItemByPage(
        page: Int,
        size: Int,
    ): ApiResult<CartResponse>

    suspend fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
    ): ApiResult<CartResponse>

    suspend fun fetchAuthCode(validKey: String): ApiResult<Int>

    suspend fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): ApiResult<Int>

    suspend fun deleteItem(cartId: Int): ApiResult<Int>

    suspend fun addItem(
        itemId: Int,
        itemCount: Int,
    ): ApiResult<AddItemResult>
}
