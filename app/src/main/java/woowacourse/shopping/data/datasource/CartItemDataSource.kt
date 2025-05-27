package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemDataSource {
    fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (CartItemsResponse?) -> Unit,
    )

    fun submitCartItem(cartItem: CartItemRequest)

    fun removeCartItem(id: Int)

    fun updateCartItem(quantity: Quantity)

    fun fetchCartItemsCount(onResult: (Quantity?) -> Unit)
}
