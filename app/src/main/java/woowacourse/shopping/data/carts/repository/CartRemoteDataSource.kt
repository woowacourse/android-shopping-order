package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse

interface CartRemoteDataSource {
    suspend fun fetchCartItemByPage(
        page: Int,
        size: Int,
    ): CartFetchResult<CartResponse>

    suspend fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
    ): CartFetchResult<CartResponse>

    fun fetchAuthCode(
        validKey: String,
        onResponse: (Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    )

    suspend fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): CartUpdateResult<Int>

    suspend fun deleteItem(cartId: Int): CartFetchResult<Int>

    suspend fun addItem(
        itemId: Int,
        itemCount: Int,
    ): CartFetchResult<AddItemResult>
}
