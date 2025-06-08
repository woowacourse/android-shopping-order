package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage

interface CartRepository {
    suspend fun addCart(cart: Cart): Result<Long>

    suspend fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit>

    suspend fun deleteCart(cartId: Long): Result<Unit>

    suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): Result<CartsSinglePage>

    suspend fun cartQuantity(): Result<Int>
}
