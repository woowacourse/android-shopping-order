package woowacourse.shopping.data.cart.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.data.dto.request.CartSaveRequest
import woowacourse.shopping.data.dto.request.CartUpdateRequest
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse
import woowacourse.shopping.data.remote.ApiClient
import woowacourse.shopping.domain.Cart

class RemoteCartDataSource(
    private val cartApiService: CartApiService =
        ApiClient.getApiClient().create(CartApiService::class.java),
) : CartDataSource {
    override fun getCartItems(
        startPage: Int,
        pageSize: Int,
        callBack: (NetworkResult<List<Cart>>) -> Unit,
    ) {
        cartApiService.requestCartItems(page = startPage, size = pageSize).enqueue(
            object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful) {
                        val cartData =
                            response.body() ?: run {
                                callBack(NetworkResult.Error)
                                return
                            }
                        val carts = cartData.cartDto.map { it.toCart() }
                        callBack(NetworkResult.Success(carts))
                    }
                }

                override fun onFailure(
                    call: Call<CartResponse>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }

    override fun saveCartItem(
        productId: Long,
        quantity: Int,
        callBack: (NetworkResult<Long>) -> Unit,
    ) {
        val request = CartSaveRequest(productId, quantity)
        cartApiService.requestAddCartItems(request).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val location = response.headers()["Location"]
                        if (location != null) {
                            val segments = location.split("/")
                            val cartId = segments.last().toLong()
                            callBack(NetworkResult.Success(cartId))
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }

    override fun updateCartItemQuantity(
        cartId: Int,
        newQuantity: Int,
        callBack: (NetworkResult<Unit>) -> Unit,
    ) {
        val request = CartUpdateRequest(newQuantity)
        cartApiService.requestUpdateCartItems(cartId, request).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) callBack(NetworkResult.Success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }

    override fun deleteCartItem(
        cartId: Int,
        callBack: (NetworkResult<Unit>) -> Unit,
    ) {
        cartApiService.requestDeleteCartItems(cartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callBack(NetworkResult.Success(Unit))
                        return
                    }
                    callBack(NetworkResult.Error)
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }

    override fun getTotalCount(callBack: (NetworkResult<Int>) -> Unit) {
        cartApiService.requestCartItemsCount().enqueue(
            object : Callback<CartQuantityResponse> {
                override fun onResponse(
                    call: Call<CartQuantityResponse>,
                    response: Response<CartQuantityResponse>,
                ) {
                    if (response.isSuccessful) {
                        val count = response.body()?.quantity ?: 0
                        callBack(NetworkResult.Success(count))
                    }
                }

                override fun onFailure(
                    call: Call<CartQuantityResponse>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }
}
