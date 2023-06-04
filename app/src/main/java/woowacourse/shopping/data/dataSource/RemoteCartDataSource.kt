package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.service.RetrofitCartService
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.model.CartProduct

class RemoteCartDataSource(
    private val service: RetrofitCartService = RetrofitClient.getInstance().retrofitCartService,
) : CartDataSource {
    override fun getAll(callback: (List<CartProduct>?) -> Unit) {
        service.getCarts().enqueue(
            object : retrofit2.Callback<List<CartProduct>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CartProduct>>,
                    response: retrofit2.Response<List<CartProduct>>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<List<CartProduct>>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    override fun postItem(itemId: Int, callback: (Int?) -> Unit) {
        service.postCart(itemId).enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>,
                ) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    }
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Int?) -> Unit) {
        service.patchCart(itemId, quantity).enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    override fun deleteItem(itemId: Int, callback: (Int?) -> Unit) {
        service.deleteCart(itemId).enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }
}
