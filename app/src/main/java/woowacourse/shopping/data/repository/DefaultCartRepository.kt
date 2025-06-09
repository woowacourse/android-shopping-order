package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val dataSource: CartDataSource,
) : CartRepository {
    override suspend fun addCart(cart: Cart): Result<Long> {
        return dataSource.addCart(cart.toRequest())
    }

    override suspend fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
    ): Result<CartsSinglePage> {
        return runCatching {
            val response = dataSource.singlePage(page, pageSize).getOrThrow()
            response.toDomain()
        }
    }

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit> {
        return runCatching {
            dataSource.updateCartQuantity(cartId, quantity.value)
        }.mapCatching { response ->
            response.onSuccess { Result.success(Unit) }
            response.onFailure { Result.failure<Unit>(NullPointerException("Response was null")) }
        }
    }

    override suspend fun deleteCart(cartId: Long): Result<Unit> {
        return runCatching {
            dataSource.deleteCart(cartId)
        }.mapCatching { response ->
            response.onSuccess { Result.success(Unit) }
            response.onFailure { Result.failure<Unit>(NullPointerException("Response was null")) }
        }
    }
}
