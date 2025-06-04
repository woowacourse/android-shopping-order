package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val cartDataSource: CartDataSource,
) : CartRepository {
    override suspend fun addCart(cart: Cart): NetworkResult<Long> =
        withContext(Dispatchers.IO) {
            cartDataSource.addCart(cart.toRequest())
        }

    override suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): NetworkResult<CartsSinglePage> =
        withContext(Dispatchers.IO) {
            cartDataSource.singlePage(page, pageSize)
        }

    override fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    ) {
        cartDataSource.updateCartQuantity(cartId, quantity.value) { callback(it) }
    }

    override fun deleteCart(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    ) {
        cartDataSource.deleteCart(cartId) { callback(it) }
    }
}
