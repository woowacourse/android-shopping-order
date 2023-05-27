package woowacourse.shopping.service

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody
import woowacourse.shopping.utils.RetrofitUtil

class RemoteCartService(baseUrl: String) {
    private var credentials = "YUBhLmNvbToxMjM0"

    fun getAll(callback: (List<CartProduct>?) -> Unit) {
        RetrofitUtil.retrofitCartService.getCarts("Basic $credentials").enqueue(
            object : retrofit2.Callback<List<CartProduct>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CartProduct>>,
                    response: retrofit2.Response<List<CartProduct>>
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<List<CartProduct>>, t: Throwable) {
                    callback(null)
                }
            }
        )
    }

    fun postItem(itemId: Int, callback: (Int?) -> Unit) {
        RetrofitUtil.retrofitCartService.postCart(
            "Basic $credentials",
            ProductIdBody(itemId)
        ).enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>
                ) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    }
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(null)
                }
            }
        )
    }

    fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Int?) -> Unit) {
        RetrofitUtil.retrofitCartService.patchCart(
            itemId,
            "Basic $credentials",
            QuantityBody(quantity)
        ).enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(null)
                }
            }
        )
    }

    fun deleteItem(itemId: Int, callback: (Int?) -> Unit) {
        RetrofitUtil.retrofitCartService.deleteCart(itemId, "Basic $credentials").enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(null)
                }
            }
        )
    }
}
