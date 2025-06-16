package woowacourse.shopping.data.remote.cart

import retrofit2.HttpException
import retrofit2.Response

class CartRepository(
    private val cartService: CartService,
) {
    suspend fun fetchAllCart(): Result<CartResponse?> =
        runCatching {
            val response = cartService.requestCart(page = null, size = null)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }

    suspend fun fetchCart(
        page: Int?,
        size: Int?,
    ): Result<CartResponse?> =
        runCatching {
            val response = cartService.requestCart(page = page, size = size)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }

    suspend fun addToCart(cartRequest: CartRequest): Result<Long> =
        runCatching {
            val response = cartService.addToCart(cartRequest = cartRequest)

            val locationHeader =
                response
                    .headers()["Location"]
                    ?.split("/")
                    ?.last()
                    ?.toLongOrNull()
            if (locationHeader == null) throw Exception("Location header not found")
            locationHeader
        }

    suspend fun deleteCart(id: Long): Result<Response<Unit>> =
        runCatching {
            val response = cartService.deleteFromCart(id = id)
            if (response.isSuccessful) {
                response
            } else {
                throw HttpException(response)
            }
        }

    suspend fun updateCart(
        id: Long,
        cartQuantity: CartQuantity,
    ): Result<Response<Unit>> =
        runCatching {
            val response = cartService.updateCart(id = id, cartQuantity = cartQuantity)
            if (response.isSuccessful) {
                response
            } else {
                throw HttpException(response)
            }
        }

    suspend fun getCartCounts(): Result<CartQuantity> =
        runCatching {
            val response = cartService.getCartCounts()
            if (response.isSuccessful) {
                response.body() ?: throw HttpException(response)
            } else {
                throw HttpException(response)
            }
        }
}
