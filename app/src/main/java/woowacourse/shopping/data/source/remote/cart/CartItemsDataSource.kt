package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.ItemCount

interface CartItemsDataSource {
    fun getCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<CartItemResponse>) -> Unit,
    )

    fun addCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Long>) -> Unit,
    )

    fun deleteCartItem(
        id: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun updateCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun getCarItemsCount(onResult: (Result<ItemCount>) -> Unit)
}
