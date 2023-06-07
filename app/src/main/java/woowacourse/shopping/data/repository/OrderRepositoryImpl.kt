package woowacourse.shopping.data.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderMinInfoItem
import com.example.domain.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.remote.OrderService
import woowacourse.shopping.data.model.dto.request.OrderRequestDto
import woowacourse.shopping.data.model.dto.response.OrderDetailDto
import woowacourse.shopping.data.model.dto.response.OrderMinInfoItemDto
import woowacourse.shopping.data.util.responseParseCustomError
import java.net.URI

class OrderRepositoryImpl(
    private val orderService: OrderService
) : OrderRepository {
    override fun fetchAllOrders(callBack: (BaseResponse<List<OrderMinInfoItem>>) -> Unit) {
        orderService.getOrders().enqueue(object : Callback<List<OrderMinInfoItemDto>> {
            override fun onResponse(
                call: Call<List<OrderMinInfoItemDto>>,
                response: Response<List<OrderMinInfoItemDto>>
            ) {
                if (response.isSuccessful.not())
                    return responseParseCustomError(response.errorBody(), callBack)

                callBack(
                    BaseResponse.SUCCESS(
                        response.body()?.map { it.toDomain() }
                            ?: emptyList()
                    )
                )
            }

            override fun onFailure(call: Call<List<OrderMinInfoItemDto>>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }

    override fun fetchOrderDetailById(
        orderId: Long,
        callBack: (BaseResponse<OrderDetail>) -> Unit
    ) {
        orderService.getOrderDetail(orderId).enqueue(object : Callback<OrderDetailDto> {
            override fun onResponse(
                call: Call<OrderDetailDto>,
                response: Response<OrderDetailDto>
            ) {
                if (response.isSuccessful.not() || response.body() == null)
                    return responseParseCustomError(response.errorBody(), callBack)

                response.body()?.let {
                    return callBack(BaseResponse.SUCCESS(it.toDomain()))
                }
            }

            override fun onFailure(call: Call<OrderDetailDto>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }

    override fun addOrder(
        cartIds: List<Long>,
        orderPaymentPrice: Int,
        callBack: (orderId: BaseResponse<Long>) -> Unit
    ) {
        orderService.addOrder(OrderRequestDto(cartIds, orderPaymentPrice))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not())
                        return responseParseCustomError(response.errorBody(), callBack)

                    val responseHeader = response.headers()["Location"]
                        ?: return responseParseCustomError(response.errorBody(), callBack)

                    val orderId = URI(responseHeader).path.substringAfterLast("/").toLong()
                    callBack(BaseResponse.SUCCESS(orderId))
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callBack(BaseResponse.NETWORK_ERROR())
                }
            })
    }
}
