package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody
import woowacourse.shopping.utils.RetrofitUtil

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    private var credentials = "YUBhLmNvbToxMjM0"

    override fun getAll(callback: (Result<List<CartProduct>>) -> Unit) {
        RetrofitUtil.retrofitCartService.getCarts("Basic $credentials").enqueue(
            object : retrofit2.Callback<List<CartProduct>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CartProduct>>,
                    response: retrofit2.Response<List<CartProduct>>
                ) {
                    when (response.code()) {
                        200 -> callback(
                            Result.success(response.body() ?: throw Throwable("Not Found"))
                        )
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<CartProduct>>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }

    override fun postItem(itemId: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService.postCart(
            "Basic $credentials",
            ProductIdBody(itemId)
        ).enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>
                ) {
                    when (response.code()) {
                        201 -> callback(
                            Result.success(response.body() ?: throw Throwable("Not Found"))
                        )
                        409 -> callback(Result.failure(Throwable("Already Exists")))
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService.patchCart(
            itemId,
            "Basic $credentials",
            QuantityBody(quantity)
        ).enqueue(
            object : retrofit2.Callback<Void> {
                override fun onResponse(
                    call: retrofit2.Call<Void>,
                    response: retrofit2.Response<Void>
                ) {
                    when (response.code()) {
                        200 -> callback(Result.success(quantity))
                        404 -> callback(Result.failure(Throwable("Not Found")))
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }

    override fun deleteItem(itemId: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService.deleteCart(itemId, "Basic $credentials").enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: retrofit2.Call<Int>,
                    response: retrofit2.Response<Int>
                ) {
                    when (response.code()) {
                        200 -> callback(
                            Result.success(response.body() ?: throw Throwable("Not Found"))
                        )
                        404 -> callback(Result.failure(Throwable("Not Found")))
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }
}
