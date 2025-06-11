package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.ItemCount

interface CartItemsDataSource {
    suspend fun getCartItems(
        page: Int?,
        size: Int?,
    ): Result<CartItemResponse>

    suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Long>

    suspend fun deleteCartItem(id: Long): Result<Unit>

    suspend fun updateCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCarItemsCount(): Result<ItemCount>
}
