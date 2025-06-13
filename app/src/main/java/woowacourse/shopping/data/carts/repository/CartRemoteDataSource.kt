package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse

interface CartRemoteDataSource {

    suspend fun fetchCartItemByPage(
        page: Int,
        size: Int,
    ): CartResponse

    suspend fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
    ): CartResponse

    suspend fun fetchCartCount(): Int

    suspend fun fetchAuthCode(
        validKey: String,
    ): Int

    suspend fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
    )

    suspend fun deleteItem(
        cartId: Int,
    )

    suspend fun addItem(
        itemId: Int,
        itemCount: Int,
    )
}