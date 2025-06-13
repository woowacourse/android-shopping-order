package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override suspend fun addCart(cart: Cart): Result<Long> = remoteCartDataSource.addCart(cart.toRequest())

    override suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): Result<CartsSinglePage> = remoteCartDataSource.singlePage(page, pageSize)

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit> = remoteCartDataSource.updateCartQuantity(cartId, quantity.value)

    override suspend fun deleteCart(cartId: Long): Result<Unit> = remoteCartDataSource.deleteCart(cartId)

    override suspend fun cartQuantity(): Result<Int> = remoteCartDataSource.cartQuantity()
}
