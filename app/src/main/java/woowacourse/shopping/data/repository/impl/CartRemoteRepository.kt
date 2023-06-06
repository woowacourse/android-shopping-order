package woowacourse.shopping.data.repository.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.remote.CartApi
import woowacourse.shopping.data.remote.RequestInsertBody
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ServerStoreRespository
import woowacourse.shopping.domain.model.CartProduct

class CartRemoteRepository(
    serverRepository: ServerStoreRespository,
) : CartRepository {

    private val cartService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), CartApi::class.java)

    override fun getAll(callback: (DataResult<List<CartProduct>>) -> Unit) {
        cartService.requestCartItems().enqueue(object : retrofit2.Callback<List<CartProduct>> {
            override fun onResponse(
                call: Call<List<CartProduct>>,
                response: Response<List<CartProduct>>,
            ) {
                if (!response.isSuccessful) {
                    callback(DataResult.NotSuccessfulError)
                    return
                }
                response.body()?.let { cartProducts ->
                    callback(DataResult.Success(cartProducts))
                }
            }

            override fun onFailure(call: Call<List<CartProduct>>, t: Throwable) {
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
                    callback(DataResult.Success(cartId ?: -1))
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
