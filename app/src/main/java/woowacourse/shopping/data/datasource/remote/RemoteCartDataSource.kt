package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkError

class RemoteCartDataSource(
    private val service: CartService,
    private val handler: NetworkResultHandler,
) {
    suspend fun addCart(request: CartItemRequest): Result<Long> =
        runCatching {
            val response = service.addCart(request)
            val location =
                response.headers()["Location"]
                    ?.substringAfterLast("/")
                    ?.toLongOrNull()
            requireNotNull(location) { throw NetworkError.MissingLocationHeaderError }
            location
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { handler.handleException(it) },
        )

    suspend fun singlePage(
        page: Int?,
        size: Int?,
    ): Result<CartsSinglePage> =
        runCatching {
            val response = service.getCartSinglePage(page, size)
            response.toDomain()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { handler.handleException(it) },
        )

    suspend fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            service.updateCart(cartId, quantity)
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { handler.handleException(it) },
        )

    suspend fun deleteCart(cartId: Long): Result<Unit> =
        runCatching {
            service.deleteCart(cartId)
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { handler.handleException(it) },
        )

    suspend fun cartQuantity(): Result<Int> =
        runCatching {
            service.getCartQuantity().quantity
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { handler.handleException(it) },
        )
}
