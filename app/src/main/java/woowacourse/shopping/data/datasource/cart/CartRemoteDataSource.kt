package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.model.response.CartItemsQuantityResponse
import woowacourse.shopping.data.model.response.CartItemsResponse

interface CartRemoteDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartItemsResponse

    suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    )

    suspend fun deleteCartItem(cartId: Long)

    suspend fun patchCartItem(
        cartId: Long,
        quantity: Int,
    )

    suspend fun getCartItemsCount(): CartItemsQuantityResponse
}
