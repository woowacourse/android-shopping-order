package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.retrofit.CartApi
import woowacourse.shopping.data.retrofit.RequestInsertBody
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ServerStoreRespository

class CartRemoteRepository(serverRepository: ServerStoreRespository, private val failureCallback: (String?) -> Unit) : CartRepository {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(serverRepository.getServerUrl())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CartApi::class.java)

    override fun getAll(callback: (List<CartProduct>) -> Unit) {
        retrofitService.requestCartItems().enqueue(object : retrofit2.Callback<List<CartProduct>> {
            override fun onResponse(
                call: Call<List<CartProduct>>,
                response: Response<List<CartProduct>>,
            ) {
                response.body()?.let { cartProducts ->
                    callback(cartProducts)
                }
            }

            override fun onFailure(call: Call<List<CartProduct>>, t: Throwable) {
                failureCallback(t.message)
            }
        })
    }

    override fun insert(productId: Int, quantity: Int, callback: (cartItemId: Int) -> Unit) {
        retrofitService.requestInsertCart(RequestInsertBody(productId, quantity))
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    val cartId = response.headers()["Location"]?.substringAfterLast("/")?.toInt()
                    callback(cartId ?: -1)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    failureCallback(t.message)
                }
            })
    }

    override fun update(cartId: Int, quantity: Int, callback: (Boolean) -> Unit) {
        retrofitService.requestUpdateCart(cartId, quantity)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    callback(response.isSuccessful)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    failureCallback(t.message)
                }
            })
    }

    override fun remove(cartId: Int, callback: (Boolean) -> Unit) {
        retrofitService.requestDeleteCart(cartId).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                failureCallback(t.message)
            }
        })
    }
}
