package woowacourse.shopping.data.repository.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.remote.CartApi
import woowacourse.shopping.data.remote.RequestInsertBody
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.CartProductDTO
import woowacourse.shopping.data.remote.dto.OrderSubmitDTO
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ServerStoreRespository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order

class CartRemoteRepository(
    serverRepository: ServerStoreRespository,
) : CartRepository {

    private val cartService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), CartApi::class.java)

    override fun getAll(callback: (DataResult<List<CartProduct>>) -> Unit) {
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
                    callback(DataResult.Success(cartProducts.map { it.toDomain() }))
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
