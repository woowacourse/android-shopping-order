package woowacourse.shopping.data.datasource

import retrofit2.HttpException
import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.domain.exception.NetworkResult

class CartDataSource(
    private val service: CartService,
    private val handler: NetworkResultHandler = NetworkResultHandler(),
) {
    suspend fun addCart(request: CartItemRequest): NetworkResult<Long> =
        handler.execute {
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
    ): NetworkResult<CartsSinglePage> = handler.execute { service.getCartSinglePage(page, size).toDomain() }

    suspend fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
    ): NetworkResult<Unit> = handler.execute { service.updateCart(cartId, quantity) }

    suspend fun deleteCart(cartId: Long): NetworkResult<Unit> = handler.execute { service.deleteCart(cartId) }
}
