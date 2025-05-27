package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse

interface CartItemsDataSource {
    fun getCartItems(onResult: (Result<CartItemResponse>) -> Unit)

    fun addCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<String>) -> Unit,
    )

    fun deleteCartItem(
        id: Int,
        onResult: (Result<String>) -> Unit,
    )

    fun updateCartItem(
        id: Int,
        onResult: (Result<String>) -> Unit,
    )

    fun getCarItemsCount(onResult: (Result<CartItemResponse>) -> Unit)
}