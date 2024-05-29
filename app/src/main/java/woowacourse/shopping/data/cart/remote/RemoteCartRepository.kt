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
