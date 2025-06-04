package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.response.carts.CartsResponse
import woowacourse.shopping.data.network.service.CartService

class CartDataSource(
    private val service: CartService,
) {
    fun addCart(
        request: CartItemRequest,
        callback: (Result<String?>) -> Unit,
    ) {
        val result = service.addCart(request)
        result.enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val cartId =
                            response
                                .headers()["Location"]
                                ?.split("/")
                                ?.last()

                        callback(Result.success(cartId))
                    } else {
                        callback(Result.failure(NullPointerException()))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    fun singlePage(
        page: Int?,
        size: Int?,
        callback: (Result<CartsResponse?>) -> Unit,
    ) {
        service.getCartSinglePage(page, size).enqueue(
            object : Callback<CartsResponse> {
                override fun onResponse(
                    call: Call<CartsResponse>,
                    response: Response<CartsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        callback(Result.success(body))
                    } else {
                        callback(Result.failure(NullPointerException("응답이 비어 있습니다.")))
                    }
                }

                override fun onFailure(
                    call: Call<CartsResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
        callback: (Result<Unit?>) -> Unit,
    ) {
        service.updateCart(cartId, quantity).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit?>,
                    response: Response<Unit?>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        callback(Result.failure(HttpException(response)))
                    }
                }

                override fun onFailure(
                    call: Call<Unit?>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    fun deleteCart(
        cartId: Long,
        callback: (Result<Unit?>) -> Unit,
    ) {
        service.deleteCart(cartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit?>,
                    response: Response<Unit?>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        callback(Result.failure(HttpException(response)))
                    }
                }

                override fun onFailure(
                    call: Call<Unit?>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }
}
