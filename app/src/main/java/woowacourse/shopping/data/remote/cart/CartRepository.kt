package woowacourse.shopping.data.remote.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.cart.CartResponse.Content
import kotlin.collections.orEmpty

class CartRepository {
    fun fetchCart(
        onSuccess: (List<Content>) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        CartClient.getRetrofitService().requestCart().enqueue(
            object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.content.orEmpty()
                        onSuccess(result)
                    } else {
                        val errorMessage =
                            buildString {
                                append("응답 실패: ${response.code()}")
                                response.errorBody()?.let {
                                    append(" - ${it.string()}")
                                }
                            }
                        Log.e("CartRepository", errorMessage)
                        onError(Throwable(errorMessage))
                    }
                }

                override fun onFailure(
                    call: Call<CartResponse>,
                    t: Throwable,
                ) {
                    Log.e("CartRepository", "네트워크 실패", t)
                    onError(t)
                }
            },
        )
    }

    fun addToCart(
        cartRequest: CartRequest,
        onResult: (Result<Unit>) -> Unit,
    ) {
        CartClient
            .getRetrofitService()
            .addToCart(cartRequest = cartRequest)
            .enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>,
                    ) {
                        if (response.isSuccessful) {
                            onResult(Result.success(Unit))
                        } else {
                            val error = response.errorBody()?.string()
                            onResult(Result.failure(Throwable("추가 실패: ${response.code()} - $error")))
                        }
                    }

                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    fun updateCart(
        id: Long,
        cartQuantity: CartQuantity,
        onResult: (Result<Unit>) -> Unit,
    ) {
        CartClient
            .getRetrofitService()
            .updateCart(id = id, cartQuantity = cartQuantity)
            .enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void?>,
                        response: Response<Void?>,
                    ) {
                        if (response.isSuccessful) {
                            onResult(Result.success(Unit))
                        } else {
                            val error = response.errorBody()?.string()
                            onResult(Result.failure(Throwable("수정 실패: ${response.code()} - $error")))
                        }
                    }

                    override fun onFailure(
                        call: Call<Void?>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    fun getCartCounts(
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        CartClient
            .getRetrofitService()
            .getCartCounts()
            .enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity?>,
                        response: Response<CartQuantity?>,
                    ) {
                        if (response.isSuccessful) {
                            val totalCount = response.body()?.quantity?.toLong() ?: 0L
                            onSuccess(totalCount)
                        } else {
                            val errorMessage =
                                buildString {
                                    append("응답 실패: ${response.code()}")
                                    response.errorBody()?.let {
                                        append(" - ${it.string()}")
                                    }
                                }
                            Log.e("CartRepository", errorMessage)
                            onError(Throwable(errorMessage))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity?>,
                        t: Throwable,
                    ) {
                        Log.e("CartRepository", "장바구니 조회 실패", t)
                        onError(t)
                    }
                },
            )
    }
}
