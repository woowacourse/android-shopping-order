package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.cartitem.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemDataSource {
    suspend fun fetchPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): CartItemResponse

    suspend fun submitCartItem(cartItem: CartItemRequest)

    suspend fun removeCartItem(cartId: Long)

    suspend fun updateCartItem(
        cartId: Long,
        quantity: Quantity,
    )

    suspend fun fetchCartItemsCount(): Quantity
}
