package woowacourse.shopping.data.datasource

import retrofit2.HttpException
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.response.carts.CartsResponse
import woowacourse.shopping.data.network.service.CartService

class CartDataSource(
    private val service: CartService,
) {
    suspend fun addCart(request: CartItemRequest): Result<Long> {
        return runCatching {
            val response = service.addCart(request)

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            val locationHeader = response.headers()["Location"]
                ?: throw IllegalStateException("Missing Location header")

            locationHeader.split("/").last().toLong()
        }
    }

    suspend fun singlePage(
        page: Int?,
        size: Int?,
    ): Result<CartsResponse> {
        return runCatching {
            service.getCartSinglePage(page, size)
        }
    }

    suspend fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            val response = service.updateCart(cartId, quantity)
            if (!response.isSuccessful) {
                Result.failure<Unit>(HttpException(response))
            }
        }
    }

    suspend fun deleteCart(
        cartId: Long,
    ): Result<Unit> {
        return runCatching {
            val response = service.deleteCart(cartId)
            if (!response.isSuccessful) {
                Result.failure<Unit>(HttpException(response))
            }
        }
    }
}
