package woowacourse.shopping.remote.service

import woowacourse.shopping.remote.model.request.CartItemRequest
import woowacourse.shopping.remote.model.response.CartItemListResponse

interface CartApiService {
    suspend fun requestCartItems(page: Int? = null, size: Int? = null): CartItemListResponse

    suspend fun addCartItem(cartItemRequest: CartItemRequest)

    suspend fun updateCartItemQuantity(id: Long, quantity: Int)

    suspend fun removeCartItem(id: Long)
}
