package woowacourse.shopping.data.datasource

import retrofit2.HttpException
import woowacourse.shopping.data.ApiCallbackHandler
import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.domain.exception.NetworkResult

class CartDataSource(
    private val service: CartService,
    private val handler: ApiCallbackHandler,
    private val handler2: NetworkResultHandler = NetworkResultHandler(),
) {
    suspend fun addCart(request: CartItemRequest): NetworkResult<Long> =
        handler2.execute {
            val response = service.addCart(request)
            if (!response.isSuccessful) throw HttpException(response)

            val location =
                response
                    .headers()["Location"]
                    ?.split("/")
                    ?.last()
                    ?.toLongOrNull()

            if (location == null) throw NetworkError.MissingLocationHeaderError

            location
        }

    suspend fun singlePage(
        page: Int?,
        size: Int?,
    ): NetworkResult<CartsSinglePage> = handler2.execute { service.getCartSinglePage(page, size).toDomain() }

    suspend fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
    ) = handler2.execute { service.updateCart(cartId, quantity) }

    fun deleteCart(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    ) = handler.enqueueWithResult(service.deleteCart(cartId), callback)
}
