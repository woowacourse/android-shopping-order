package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val dataSource: CartDataSource,
) : CartRepository {
    override suspend fun addCart(cart: Cart): Result<Long> = withContext(Dispatchers.IO) {
        runCatching { dataSource.addCart(cart.toRequest()) }
    }

    override suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): Result<CartsSinglePage> = withContext(Dispatchers.IO) {
        runCatching {
            val response = dataSource.singlePage(page, pageSize)
            response.toDomain()
        }
    }

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.updateCartQuantity(cartId, quantity.value)
        }
    }

    override suspend fun deleteCart(cartId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.deleteCart(cartId)
        }
    }
}
