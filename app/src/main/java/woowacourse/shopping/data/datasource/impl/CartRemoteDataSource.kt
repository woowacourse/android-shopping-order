package woowacourse.shopping.data.datasource.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.remote.CartApi
import woowacourse.shopping.data.remote.RequestInsertBody
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.CartProductDTO
import woowacourse.shopping.data.remote.result.DataResult

class CartRemoteDataSource(url: String) : CartDataSource {
    private val cartService =
        RetrofitGenerator.create(url, CartApi::class.java)

    override fun getAll(callback: (DataResult<List<CartProductDTO>>) -> Unit) {
        cartService.requestCartItems().enqueue(object : retrofit2.Callback<List<CartProductDTO>> {
            override fun onResponse(
                call: Call<List<CartProductDTO>>,
                response: Response<List<CartProductDTO>>,
            ) {
                if (!response.isSuccessful) {
                    callback(DataResult.NotSuccessfulError)
                    return
                }
                response.body()?.let { cartProducts ->
                    if (!cartProducts.all { it.isNotNull }) {
                        callback(DataResult.WrongResponse)
                        return
                    }
                    callback(DataResult.Success(cartProducts))
                }
            }

            override fun onFailure(call: Call<List<CartProductDTO>>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }

    override fun insert(productId: Int, quantity: Int, callback: (DataResult<Int>) -> Unit) {
        cartService.requestInsertCart(RequestInsertBody(productId, quantity))
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    val cartId = response.headers()["Location"]?.substringAfterLast("/")?.toInt()
                    if (cartId == null) {
                        callback(DataResult.WrongResponse)
                        return
                    }
                    callback(DataResult.Success(cartId))
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }

    override fun update(cartId: Int, quantity: Int, callback: (DataResult<Boolean>) -> Unit) {
        cartService.requestUpdateCart(cartId, quantity)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    callback(DataResult.Success(response.isSuccessful))
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }

    override fun remove(cartId: Int, callback: (DataResult<Boolean>) -> Unit) {
        cartService.requestDeleteCart(cartId).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback(DataResult.NotSuccessfulError)
                    return
                }
                callback(DataResult.Success(response.isSuccessful))
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(DataResult.Failure)
            }
        })
    }
}
