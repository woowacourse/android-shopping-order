package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.CartItem

interface CartDataSource {
    fun getTotalCount(onResult: (Result<Int>) -> Unit)

    fun getPagedCartItems(
        page: Int,
        size: Int,
        onResult: (List<CartItem>) -> Unit,
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

//    fun deleteProductById(productId: Long)
}
