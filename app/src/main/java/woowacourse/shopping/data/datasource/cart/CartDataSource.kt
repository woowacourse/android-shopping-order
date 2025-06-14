package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page

interface CartDataSource {
    suspend fun fetchPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): Page<CartItem>

    suspend fun submitCartItem(
        productId: Long,
        quantity: Int,
    )

    suspend fun removeCartItem(cartId: Long)

    suspend fun removeCartItems(cartIds: List<Long>)

    suspend fun updateCartItem(
        cartId: Long,
        quantity: Int,
    )

    suspend fun fetchCartItemsCount(): Int
}
