package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse

interface CartItemsDataSource {
    fun getCartItems(
        page: Int,
        size: Int,
        onResult: (Result<CartItemResponse>) -> Unit,
    )

    fun addCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun deleteCartItem(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun updateCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun getCarItemsCount(onResult: (Result<Int>) -> Unit)
}
