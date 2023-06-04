package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.retrofit.CartApi
import woowacourse.shopping.data.retrofit.RequestInsertBody
import woowacourse.shopping.data.retrofit.RetrofitGenerator
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ServerStoreRespository

class CartRemoteRepository(
    serverRepository: ServerStoreRespository,
) : CartRepository {

    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), CartApi::class.java)

    override fun getAll(callback: (List<CartProduct>) -> Unit) {
        retrofitService.requestCartItems().enqueue(object : retrofit2.Callback<List<CartProduct>> {
            override fun onResponse(
                call: Call<List<CartProduct>>,
                response: Response<List<CartProduct>>,
            ) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                response.body()?.let { cartProducts ->
                    callback(cartProducts)
                }
            }

            override fun onFailure(call: Call<List<CartProduct>>, t: Throwable) {
                throw t
            }
        })
    }

    override fun insert(productId: Int, quantity: Int, callback: (Int) -> Unit) {
        retrofitService.requestInsertCart(RequestInsertBody(productId, quantity))
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                        return
                    }
                    val cartId = response.headers()["Location"]?.substringAfterLast("/")?.toInt()
                    callback(cartId ?: -1)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    throw t
                }
            })
    }

    override fun update(cartId: Int, quantity: Int, callback: (Boolean) -> Unit) {
        retrofitService.requestUpdateCart(cartId, quantity)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                        return
                    }
                    callback(response.isSuccessful)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    throw t
                }
            })
    }

    override fun remove(cartId: Int, callback: (Boolean) -> Unit) {
        retrofitService.requestDeleteCart(cartId).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                throw t
            }
        })
    }

    companion object {
        private const val SERVER_ERROR_MESSAGE = "서버와의 통신이 원활하지 않습니다."
    }
}
