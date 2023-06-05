package woowacourse.shopping.data.respository.order.source.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.respository.order.service.OrderService
import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override fun requestPostData(
        order: Order,
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit
    ) {
        val orderPostEntity = OrderPostEntity(
            order.cartIds,
            order.getCardNumber(),
            order.card.cvc,
            order.usePoint.getPoint()
        )
        orderService.requestPostData(orderPostEntity).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 201) {
                    val location = response.headers()["Location"] ?: return response.errorBody()
                        ?.let { onFailure(it.string()) } ?: Unit

                    val orderId = location.substringAfterLast("orders/").toLong()
                    onSuccess(orderId)

                    return
                }
                response.errorBody()?.let { onFailure(it.string()) }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        })
    }

    override fun requestOrderItem(
        orderId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (OrderDetail) -> Unit
    ) {
        orderService.requestOrderItem(orderId)
            .enqueue(object : retrofit2.Callback<OrderDetailEntity> {
                override fun onResponse(
                    call: Call<OrderDetailEntity>,
                    response: Response<OrderDetailEntity>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.toModel())
                        } ?: response.errorBody()?.let { onFailure(it.string()) }
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<OrderDetailEntity>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                }
            })
    }

    override fun requestOrderList(
        onFailure: (message: String) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit
    ) {
        orderService.requestOrderList()
            .enqueue(object : retrofit2.Callback<List<OrderDetailEntity>> {
                override fun onResponse(
                    call: Call<List<OrderDetailEntity>>,
                    response: Response<List<OrderDetailEntity>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { orders ->
                            onSuccess(orders.map { it.toModel() })
                        } ?: response.errorBody()?.let { onFailure(it.string()) }
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<List<OrderDetailEntity>>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                }
            })
    }
}
