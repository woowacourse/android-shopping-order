package woowacourse.shopping.data.respository.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.order.service.OrderService

class OrderRepositoryImpl(
    retrofit: Retrofit,
) : OrderRepository {
    private val orderService = retrofit.create(OrderService::class.java)

    override fun requestPostOrder(
        orderPostEntity: OrderPostEntity,
        onFailure: () -> Unit,
        onSuccess: (orderId: Long) -> Unit,
    ) {
        orderService.requestPostOrder("Basic ${Server.TOKEN}", orderPostEntity)
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

    override fun requestOrderById(
        orderId: Long,
        onFailure: () -> Unit,
        onSuccess: (orderDetailEntity: OrderDetailEntity) -> Unit,
    ) {
        orderService.requestOrderById("Basic ${Server.TOKEN}", orderId)
            .enqueue(object : Callback<OrderDetailEntity> {
                override fun onResponse(
                    call: Call<OrderDetailEntity>,
                    response: Response<OrderDetailEntity>,
                ) {
                    if (response.isSuccessful) {
                        val orderDetailEntity = response.body() ?: return onFailure()
                        onSuccess(orderDetailEntity)
                    }
                }

                override fun onFailure(call: Call<OrderDetailEntity>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestOrders(
        onFailure: () -> Unit,
        onSuccess: (orderDetailEntities: List<OrderDetailEntity>) -> Unit,
    ) {
        orderService.requestOrders("Basic ${Server.TOKEN}")
            .enqueue(object : Callback<List<OrderDetailEntity>> {
                override fun onResponse(
                    call: Call<List<OrderDetailEntity>>,
                    response: Response<List<OrderDetailEntity>>,
                ) {
                    if (response.isSuccessful) {
                        val orderDetailEntities = response.body() ?: return onFailure()
                        onSuccess(orderDetailEntities)
                    }
                }

                override fun onFailure(call: Call<List<OrderDetailEntity>>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
