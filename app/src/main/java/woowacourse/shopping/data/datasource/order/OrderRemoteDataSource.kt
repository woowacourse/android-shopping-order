package woowacourse.shopping.data.datasource.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.dto.OrderResponseDto
import woowacourse.shopping.data.dto.OrderResponsesDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toDto
import woowacourse.shopping.data.model.Page
import woowacourse.shopping.data.service.order.RetrofitOrderService
import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse

class OrderRemoteDataSource(
    private val orderService: RetrofitOrderService,
) : OrderDataSource {
    override fun orderProducts(
        token: String,
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = orderService.orderProducts(token, orderRequest.toDto())
        call.enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패 ${response.code()}, token: $token")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun requestOrders(
        token: String,
        page: Page,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = orderService.requestOrders(token, page.value, page.sizePerPage)
        call.enqueue(object : retrofit2.Callback<OrderResponsesDto> {
            override fun onResponse(
                call: Call<OrderResponsesDto>,
                response: Response<OrderResponsesDto>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestOrders result : $response")
                    if (result != null) {
                        onSuccess(result.orders.map { it.toDomain() })
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<OrderResponsesDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun requestSpecificOrder(
        token: String,
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = orderService.requestSpecificOrder(token, orderId)
        call.enqueue(object : retrofit2.Callback<OrderResponseDto> {
            override fun onResponse(call: Call<OrderResponseDto>, response: Response<OrderResponseDto>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestSpecificOrder result $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<OrderResponseDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}
