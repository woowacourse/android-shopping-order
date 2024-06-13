package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.dto.response.CartQuantityResponse
import woowacourse.shopping.remote.dto.response.CartResponse

interface RemoteCartDataSource {
    suspend fun getCartItems(
        startPage: Int,
        pageSize: Int,
    ): NetworkResult<CartResponse>

    suspend fun saveCartItem(
        productId: Long,
        quantity: Int,
    ): NetworkResult<Long>

    suspend fun updateCartItemQuantity(
        cartId: Int,
        newQuantity: Int,
    ): NetworkResult<Unit>

    suspend fun deleteCartItem(cartId: Int): NetworkResult<Unit>

    suspend fun getTotalCount(): NetworkResult<CartQuantityResponse>
}
