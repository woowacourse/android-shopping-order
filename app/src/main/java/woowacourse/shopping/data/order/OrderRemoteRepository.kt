package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.domain.Order
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.utils.ServerConfiguration
import woowacourse.shopping.utils.UserData

class OrderRemoteRepository : OrderRepository {

    private val orderRemoteService = Retrofit.Builder()
        .baseUrl(ServerConfiguration.host.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OrderRemoteService::class.java)

    override fun findById(id: Long, onFinish: (Order) -> Unit) {
        orderRemoteService.requestOrder(
            "Basic ${UserData.credential}",
            id
        ).enqueue(object : retrofit2.Callback<OrderDto> {
            override fun onResponse(call: Call<OrderDto>, response: Response<OrderDto>) {
                if (response.isSuccessful.not()) return
                val order = response.body()?.toDomain() ?: return
                onFinish(order)
            }

            override fun onFailure(call: Call<OrderDto>, t: Throwable) {
            }
        })
    }

    override fun findAll(onFinish: (List<Order>) -> Unit) {
        orderRemoteService.requestOrders("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<OrderDto>> {
                override fun onResponse(
                    call: Call<List<OrderDto>>,
                    response: Response<List<OrderDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val orders = response.body()?.map { it.toDomain() } ?: return
                    onFinish(orders)
                }

                override fun onFailure(call: Call<List<OrderDto>>, t: Throwable) {
                }
            })
    }
}
