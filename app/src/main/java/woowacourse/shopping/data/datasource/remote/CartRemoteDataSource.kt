package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.CartItem

interface CartRemoteDataSource {
    fun fetchTotalCount(onResult: (Result<Int>) -> Unit)

    fun fetchPagedCartItems(
        page: Int,
        size: Int?,
        onResult: (Result<List<CartItem>>) -> Unit,
    )

    fun insertCartItem(
        productId: Long,
        quantity: Int,
        onResult: (Result<Long>) -> Unit,
    )

    fun updateQuantity(
        cartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun deleteCartItemById(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    )
}
