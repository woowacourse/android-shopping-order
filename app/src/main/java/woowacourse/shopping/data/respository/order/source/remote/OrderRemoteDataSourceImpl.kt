package woowacourse.shopping.data.respository.order.source.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.dto.request.OrderRequest
import woowacourse.shopping.data.model.dto.response.OrderDetailResponse
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
        val orderRequest = OrderRequest(
            order.cartIds,
            order.getCardNumber(),
            order.card.cvc,
            order.usePoint.getPoint()
        )
        orderService.requestPostData(orderRequest).enqueue(object : retrofit2.Callback<Unit> {
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
                onFailure(ERROR_CONNECT)
            }
        })
    }

    override fun requestOrderItem(
        orderId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (OrderDetail) -> Unit
    ) {
        orderService.requestOrderItem(orderId)
            .enqueue(object : retrofit2.Callback<OrderDetailResponse> {
                override fun onResponse(
                    call: Call<OrderDetailResponse>,
                    response: Response<OrderDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.toModel())
                        } ?: response.errorBody()?.let { onFailure(it.string()) }
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                    onFailure(ERROR_CONNECT)
                }
            })
    }

    override fun requestOrderList(
        onFailure: (message: String) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit
    ) {
        orderService.requestOrderList()
            .enqueue(object : retrofit2.Callback<List<OrderDetailResponse>> {
                override fun onResponse(
                    call: Call<List<OrderDetailResponse>>,
                    response: Response<List<OrderDetailResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { orders ->
                            onSuccess(orders.map { it.toModel() })
                        } ?: response.errorBody()?.let { onFailure(it.string()) }
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<List<OrderDetailResponse>>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                    onFailure(ERROR_CONNECT)
                }
            })
    }

    companion object {
        private const val ERROR_CONNECT = "연결에 실패하였습니다. 다시 시도해주세요"
    }
}
