package woowacourse.shopping.data.remote.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartRepository {
    fun fetchAllCart(
        onSuccess: (CartResponse) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        CartClient.getRetrofitService().requestCart(size = Int.MAX_VALUE).enqueue(
            object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        } ?: onError(Throwable("응답 본문 없음"))
                    } else {
                        onError(Throwable("응답 실패: ${response.code()}"))
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

    fun fetchCart(
        onSuccess: (CartResponse) -> Unit,
        onError: (Throwable) -> Unit,
        page: Int,
    ) {
        CartClient.getRetrofitService().requestCart(page = page).enqueue(
            object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        } ?: onError(Throwable("응답 본문 없음"))
                    } else {
                        onError(Throwable("응답 실패: ${response.code()}"))
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
        onResult: (Result<Long>) -> Unit,
    ) {
        CartClient
            .getRetrofitService()
            .addToCart(cartRequest = cartRequest)
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.isSuccessful) {
                            val newCartId = response.headers()["Location"]?.substringAfterLast("/")?.toLongOrNull() ?: 0
                            onResult(Result.success(newCartId))
                        } else {
                            val error = response.errorBody()?.string()
                            onResult(Result.failure(Throwable("추가 실패: ${response.code()} - $error")))
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    fun deleteCart(
        id: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        CartClient.getRetrofitService().deleteFromCart(id = id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit?>,
                    response: Response<Unit?>,
                ) {
                    if (response.isSuccessful) {
                        onResult(Result.success(Unit))
                    } else {
                        val error = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("수정 실패: ${response.code()} - $error")))
                    }
                }

                override fun onFailure(
                    call: Call<Unit?>,
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
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit?>,
                        response: Response<Unit?>,
                    ) {
                        if (response.isSuccessful) {
                            onResult(Result.success(Unit))
                        } else {
                            val error = response.errorBody()?.string()
                            onResult(Result.failure(Throwable("수정 실패: ${response.code()} - $error")))
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit?>,
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
