package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override suspend fun addCart(cart: Cart): NetworkResult<Long> =
        withContext(Dispatchers.IO) {
            remoteCartDataSource.addCart(cart.toRequest())
        }

    override suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): NetworkResult<CartsSinglePage> =
        withContext(Dispatchers.IO) {
            remoteCartDataSource.singlePage(page, pageSize)
        }

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
    ): NetworkResult<Unit> =
        withContext(Dispatchers.IO) {
            remoteCartDataSource.updateCartQuantity(cartId, quantity.value)
        }

    override suspend fun deleteCart(cartId: Long): NetworkResult<Unit> =
        withContext(Dispatchers.IO) {
            remoteCartDataSource.deleteCart(cartId)
        }

    override suspend fun cartQuantity(): NetworkResult<Int> =
        withContext(Dispatchers.IO) {
            remoteCartDataSource.cartQuantity()
        }
}
