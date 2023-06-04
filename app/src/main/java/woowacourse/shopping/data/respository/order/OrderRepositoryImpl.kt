package woowacourse.shopping.data.respository.order

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.presentation.view.util.RetrofitService

class OrderRepositoryImpl(
    private val server: Server,
) : OrderRepository {
    private val orderService = Retrofit.Builder()
        .baseUrl(server.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)

    override fun requestOrder(
        orderPostEntity: OrderPostEntity,
        onFailure: () -> Unit,
        onSuccess: (orderId: Long) -> Unit,
    ) {
        orderService.requestOrder("Basic ${Server.TOKEN}", orderPostEntity)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        val location = response.headers()["Location"] ?: return onFailure()
                        val orderId = location.substringAfterLast("orders/").toLong()
                        onSuccess(orderId)
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
