package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkResult

interface CartRepository {
    suspend fun addCart(cart: Cart): NetworkResult<Long>

    suspend fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
    ): NetworkResult<Unit>

    suspend fun deleteCart(cartId: Long): NetworkResult<Unit>

    suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): NetworkResult<CartsSinglePage>
}
