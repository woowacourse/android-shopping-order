package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.response.CartItemsQuantityResponse
import woowacourse.shopping.data.model.response.CartItemsResponse

interface CartRemoteDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ApiResult<CartItemsResponse>

    suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): ApiResult<Unit>

    suspend fun deleteCartItem(cartId: Long): ApiResult<Unit>

    suspend fun patchCartItem(
        cartId: Long,
        quantity: Int,
    ): ApiResult<Unit>

    suspend fun getCartItemsCount(): ApiResult<CartItemsQuantityResponse>
}
