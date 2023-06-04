package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.retrofit.OrderApi
import woowacourse.shopping.data.retrofit.RetrofitGenerator
import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.OrdersDTO
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ServerStoreRespository

class OrderRemoteRepository(serverRepository: ServerStoreRespository) : OrderRepository {

    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), OrderApi::class.java)

    override fun getAll(callback: (OrdersDTO) -> Unit) {
        retrofitService.requestOrders().enqueue(object : retrofit2.Callback<OrdersDTO> {
            override fun onResponse(
                call: Call<OrdersDTO>,
                response: Response<OrdersDTO>,
            ) {
                response.body()?.let {
                    callback(it)
                }
            }

            override fun onFailure(call: Call<OrdersDTO>, t: Throwable) {
                throw t
            }
        })
    }

    override fun getOrder(id: Int, callback: (OrderDTO) -> Unit) {
        retrofitService.requestOrderDetail(id).enqueue(object : retrofit2.Callback<OrderDTO> {
            override fun onResponse(
                call: Call<OrderDTO>,
                response: Response<OrderDTO>,
            ) {
                response.body()?.let {
                    callback(it)
                }
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                throw t
            }
        })
    }

    override fun order(cartProducts: OrderCartItemsDTO, callback: (Int?) -> Unit) {
        retrofitService.requestOrderCartItems(cartProducts).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                callback(response.headers()["Location"]?.substringAfterLast("/")?.toInt())
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
