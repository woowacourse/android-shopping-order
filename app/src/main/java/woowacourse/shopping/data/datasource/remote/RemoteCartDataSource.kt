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
        handler.handleResult {
            val response = service.addCart(request)
            val location =
                response.headers()["Location"]
                    ?.substringAfterLast("/")
                    ?.toLongOrNull()
            requireNotNull(location) { throw NetworkError.MissingLocationHeaderError }
            location
        }

    suspend fun singlePage(
        page: Int?,
        size: Int?,
    ): Result<CartsSinglePage> =
        handler.handleResult {
            service.getCartSinglePage(page, size).toDomain()
        }

    suspend fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        handler.handleResult {
            service.updateCart(cartId, quantity)
        }

    suspend fun deleteCart(cartId: Long): Result<Unit> =
        handler.handleResult {
            service.deleteCart(cartId)
        }

    suspend fun cartQuantity(): Result<Int> =
        handler.handleResult {
            service.getCartQuantity().quantity
        }
}
