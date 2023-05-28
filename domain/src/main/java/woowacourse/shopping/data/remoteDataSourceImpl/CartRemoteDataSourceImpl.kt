package woowacourse.shopping.data.remoteDataSourceImpl

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody
import woowacourse.shopping.utils.RetrofitUtil

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    private var credentials = "YUBhLmNvbToxMjM0"

    override fun getAll(callback: (Result<List<CartProduct>>) -> Unit) {
        RetrofitUtil.retrofitCartService.getCarts("Basic $credentials").enqueue(
            object : Callback<List<CartProduct>> {
                override fun onResponse(
                    call: Call<List<CartProduct>>,
                    response: Response<List<CartProduct>>
                ) {
                    when (response.code()) {
                        200 -> callback(
                            Result.success(response.body() ?: throw Throwable("Not Found"))
                        )
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: Call<List<CartProduct>>, t: Throwable) {
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
            object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    when (response.code()) {
                        201 -> callback(
                            Result.success(response.body() ?: throw Throwable("Not Found"))
                        )
                        409 -> callback(Result.failure(Throwable("Already Exists")))
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
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
            object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> callback(Result.success(quantity))
                        404 -> callback(Result.failure(Throwable("Not Found")))
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }

    override fun deleteItem(itemId: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService.deleteCart(itemId, "Basic $credentials").enqueue(
            object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    when (response.code()) {
                        200 -> callback(
                            Result.success(response.body() ?: throw Throwable("Not Found"))
                        )
                        404 -> callback(Result.failure(Throwable("Not Found")))
                        else -> callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }
}
