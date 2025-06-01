package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemDataSource {
    fun fetchPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
        onResult: (CartItemResponse?) -> Unit,
    )

    fun submitCartItem(
        cartItem: CartItemRequest,
        callback: () -> Unit,
    )

    fun removeCartItem(
        cartId: Long,
        callback: (Long) -> Unit,
    )

    fun updateCartItem(
        cartId: Long,
        quantity: Quantity,
        onResult: (Long) -> Unit,
    )

    fun fetchCartItemsCount(onResult: (Quantity?) -> Unit)
}
