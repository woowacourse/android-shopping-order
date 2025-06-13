package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.cart.CartContent
import woowacourse.shopping.data.dto.cart.CartIdResponse
import woowacourse.shopping.data.dto.cart.CartItemCountResponse

interface CartDataSource {
    suspend fun getTotalCount(): CartItemCountResponse

    suspend fun getPagedCartItems(
        page: Int,
        size: Int?,
    ): List<CartContent>

    suspend fun insertCartItem(
        productId: Long,
        quantity: Int,
    ): CartIdResponse

    suspend fun updateQuantity(
        cartId: Long,
        quantity: Int,
    )

    suspend fun deleteCartItemById(cartId: Long)
}
