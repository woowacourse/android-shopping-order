package woowacourse.shopping.data.cart.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.product.remote.retrofit.DataCallback
import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartPageAttribute
import woowacourse.shopping.domain.model.Quantity

class RemoteCartRepository {
    fun findByProductId(
        productId: Int,
        totalItemCount: Int,
        dataCallback: DataCallback<CartItem?>,
    ) {
        retrofitApi.requestCartItems(page = 0, size = totalItemCount)
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body() ?: return
                            dataCallback.onSuccess(
                                body.toCartItems().find {
                                    it.productId == productId
                                },
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        dataCallback.onFailure(t)
                    }
                },
            )
    }

    fun getAllCartItem(
        totalItemCount: Int,
        dataCallback: DataCallback<List<CartItem>>,
    ) {
        getCartItems(0, totalItemCount, dataCallback)
    }

    fun getCartItems(
        page: Int,
        pageSize: Int,
        dataCallback: DataCallback<List<CartItem>>,
    ) {
        retrofitApi.requestCartItems(page = page, size = pageSize)
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body() ?: return
                            dataCallback.onSuccess(body.toCartItems())
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        dataCallback.onFailure(t)
                    }
                },
            )
    }

    fun deleteCartItem(
        id: Int,
        dataCallback: DataCallback<Unit>,
    ) {
        retrofitApi.deleteCartItem(id = id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body)
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    fun setCartItemQuantity(
        id: Int,
        quantity: Quantity,
        dataCallback: DataCallback<Unit>,
    ) {
        retrofitApi.setCartItemQuantity(id = id, quantity = quantity.count).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body)
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    fun syncFindByProductId(
        productId: Int,
        totalItemCount: Int,
    ): Result<CartItem?> {
        val response = retrofitApi.requestCartItems(page = 0, size = totalItemCount).execute()
        return runCatching {
            val body = response.body()
            if (response.isSuccessful && body != null) {
                body.toCartItems().firstOrNull { productId == it.productId }
            } else {
                error("sync get cart quantity error")
            }
        }
    }

    fun syncGetCartQuantityCount(): Result<Int> {
        val response = retrofitApi.requestCartQuantityCount().execute()
        return runCatching {
            val body = response.body()
            if (response.isSuccessful && body != null) {
                body
            } else {
                error("sync get cart quantity error")
            }
        }
    }

    fun getCartQuantityCount(dataCallback: DataCallback<Int>) {
        retrofitApi.requestCartQuantityCount().enqueue(
            object : Callback<Int> {
                override fun onResponse(
                    call: Call<Int>,
                    response: Response<Int>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body)
                    }
                }

                override fun onFailure(
                    call: Call<Int>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    fun getCartPageAttribute(
        page: Int,
        pageSize: Int,
        dataCallback: DataCallback<CartPageAttribute>,
    ) {
        retrofitApi.requestCartItems(page = page, size = pageSize)
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body() ?: return
                            val cartPage = body.toCartPage()
                            dataCallback.onSuccess(cartPage)
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        dataCallback.onFailure(t)
                    }
                },
            )
    }
}
