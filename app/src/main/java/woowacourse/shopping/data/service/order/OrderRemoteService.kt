package woowacourse.shopping.data.service.order

import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderPreview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.request.OrderRequestDTO
import woowacourse.shopping.data.dto.response.OrderDetailDto
import woowacourse.shopping.data.dto.response.OrderPreviewDto
import woowacourse.shopping.data.service.RetrofitApiGenerator
import woowacourse.shopping.user.ServerInfo
import java.net.URI

class OrderRemoteService {
    private val authorization: String = "Basic ${ServerInfo.token}"

    fun requestAddOrder(
        cartIds: List<Long>,
        totalPrice: Int,
        onSuccess: (Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        val orderRequest = OrderRequestDTO(cartIds, totalPrice)

        RetrofitApiGenerator.orderService.requestAddOrder(
            authorization,
            orderRequest,
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val responseHeader: String =
                        response.headers()["Location"] ?: return onFailure()
                    val orderId = URI(responseHeader).path.substringAfterLast("/").toLong()
                    onSuccess(orderId)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }

    fun requestOrderDetail(
        orderId: Long,
        onSuccess: (OrderDetail) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.orderService.requestOrderDetail(authorization, orderId)
            .enqueue(object : Callback<OrderDetailDto> {
                override fun onResponse(
                    call: Call<OrderDetailDto>,
                    response: Response<OrderDetailDto>,
                ) {
                    if (response.isSuccessful) {
                        val result: OrderDetailDto? = response.body()
                        val orderDetail: OrderDetail = result?.toDomain() ?: return onFailure()
                        onSuccess(orderDetail)
                    } else {
                        onFailure()
                    }
                }

                override fun onFailure(call: Call<OrderDetailDto>, t: Throwable) {
                    onFailure()
                }
            })
    }

    fun requestAll(
        onSuccess: (List<OrderPreview>) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.orderService.requestAll(authorization)
            .enqueue(object : Callback<List<OrderPreviewDto>> {
                override fun onResponse(
                    call: Call<List<OrderPreviewDto>>,
                    response: Response<List<OrderPreviewDto>>,
                ) {
                    println("dsfsdfsdfsdfsdfsd $response")
                    if (response.isSuccessful) {
                        val result: List<OrderPreviewDto>? = response.body()
                        val orderPreviews: List<OrderPreview> =
                            result?.map { it.toDomain() } ?: return onFailure()
                        onSuccess(orderPreviews)
                    }
                }

                override fun onFailure(call: Call<List<OrderPreviewDto>>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
