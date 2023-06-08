package woowacourse.shopping.data.service.order

import com.example.domain.model.CustomError
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderPreview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.request.OrderRequestDto
import woowacourse.shopping.data.dto.response.ErrorDto
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
        onFailure: (CustomError) -> Unit,
    ) {
        val orderRequest = OrderRequestDto(cartIds, totalPrice)

        RetrofitApiGenerator.orderService.requestAddOrder(
            authorization,
            orderRequest,
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    onFailure(CustomError(ErrorDto.mapToErrorDto(response.errorBody()).message))
                }
                if (response.isSuccessful) {
                    val responseHeader: String =
                        response.headers()["Location"]
                            ?: return onFailure(CustomError("헤더가 존재하지 않습니다."))
                    val orderId = URI(responseHeader).path.substringAfterLast("/").toLong()
                    onSuccess(orderId)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(CustomError())
            }
        })
    }

    fun requestOrderDetail(
        orderId: Long,
        onSuccess: (OrderDetail) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        RetrofitApiGenerator.orderService.requestOrderDetail(authorization, orderId)
            .enqueue(object : Callback<OrderDetailDto> {
                override fun onResponse(
                    call: Call<OrderDetailDto>,
                    response: Response<OrderDetailDto>,
                ) {
                    if (!response.isSuccessful) {
                        onFailure(CustomError(ErrorDto.mapToErrorDto(response.errorBody()).message))
                    }
                    if (response.isSuccessful) {
                        val orderDetail: OrderDetail =
                            response.body()?.toDomain() ?: return onFailure(CustomError("응답 바디 매핑 실패"))
                        onSuccess(orderDetail)
                    }
                }

                override fun onFailure(call: Call<OrderDetailDto>, t: Throwable) {
                    onFailure(CustomError())
                }
            })
    }

    fun requestAll(
        onSuccess: (List<OrderPreview>) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        RetrofitApiGenerator.orderService.requestAll(authorization)
            .enqueue(object : Callback<List<OrderPreviewDto>> {
                override fun onResponse(
                    call: Call<List<OrderPreviewDto>>,
                    response: Response<List<OrderPreviewDto>>,
                ) {
                    if (!response.isSuccessful) {
                        onFailure(CustomError(ErrorDto.mapToErrorDto(response.errorBody()).message))
                    }
                    if (response.isSuccessful) {
                        val result: List<OrderPreviewDto>? = response.body()
                        val orderPreviews: List<OrderPreview> =
                            result?.map { it.toDomain() } ?: listOf()
                        onSuccess(orderPreviews)
                    }
                }

                override fun onFailure(call: Call<List<OrderPreviewDto>>, t: Throwable) {
                    onFailure(CustomError())
                }
            })
    }
}
