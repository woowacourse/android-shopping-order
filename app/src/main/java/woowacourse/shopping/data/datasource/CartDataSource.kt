package woowacourse.shopping.data.datasource

import retrofit2.HttpException
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.response.carts.CartsResponse
import woowacourse.shopping.data.network.service.CartService

class CartDataSource(
    private val service: CartService,
) {
    suspend fun addCart(request: CartItemRequest): Long {
        val response = service.addCart(request)

        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val locationHeader =
            response.headers()["Location"]
                ?: throw IllegalStateException("Missing Location header")

        return locationHeader.split("/").last().toLong()
    }

    suspend fun singlePage(
        page: Int?,
        size: Int?,
    ): CartsResponse {
        return service.getCartSinglePage(page, size)
    }

    suspend fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
    ) {
        val response = service.updateCart(cartId, quantity)
        if (!response.isSuccessful) {
            throw IllegalStateException("요청이 처리되지 않았습니다.")
        }
    }

    suspend fun deleteCart(cartId: Long) {
        val response = service.deleteCart(cartId)
        if (!response.isSuccessful) {
            throw IllegalStateException("요청이 처리되지 않았습니다.")
        }
    }
}
