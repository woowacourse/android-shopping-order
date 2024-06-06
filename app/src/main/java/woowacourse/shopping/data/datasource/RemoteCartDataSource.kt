package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse

interface RemoteCartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): CartResponse

    suspend fun addCartItem(cartItemRequestBody: CartItemRequestBody)

    suspend fun deleteCartItem(cartItemId: Int)

    suspend fun updateCartItem(
        cartItemId: Int,
        cartQuantity: CartQuantity,
    )

    suspend fun getCartTotalQuantity(): CartQuantity
}
