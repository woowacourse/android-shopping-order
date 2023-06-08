package woowacourse.shopping.data.member

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.member.response.GetOrderResponse
import woowacourse.shopping.data.member.response.GetOrderHistoryResponse
import woowacourse.shopping.data.member.response.GetPointsResponse
import woowacourse.shopping.data.dto.mapper.OrderHistoryMapper.toDomain
import woowacourse.shopping.data.dto.mapper.OrderMapper.toDomain
import woowacourse.shopping.data.server.MemberRemoteDataSource
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory

class DefaultMemberRemoteDataSource(private val service: MemberService) : MemberRemoteDataSource {
    override fun getPoints(onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        service.requestPoints().enqueue(object : Callback<GetPointsResponse> {
            override fun onResponse(call: Call<GetPointsResponse>, response: Response<GetPointsResponse>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    onSuccess(responseBody.points)
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_POINTS_FAILED })
                }
            }

            override fun onFailure(call: Call<GetPointsResponse>, t: Throwable) {
                onFailure(MESSAGE_GET_POINTS_FAILED)
            }
        })
    }

    override fun getOrderHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: (String) -> Unit) {
        service.requestOrderHistories().enqueue(object : Callback<List<GetOrderHistoryResponse>> {
            override fun onResponse(
                call: Call<List<GetOrderHistoryResponse>>,
                response: Response<List<GetOrderHistoryResponse>>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    onSuccess(responseBody.map { it.toDomain() })
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_ORDER_HISTORIES_FAILED })
                }
            }

            override fun onFailure(call: Call<List<GetOrderHistoryResponse>>, t: Throwable) {
                onFailure(MESSAGE_GET_ORDER_HISTORIES_FAILED)
            }
        })
    }

    override fun getOrder(id: Int, onSuccess: (Order) -> Unit, onFailure: (String) -> Unit) {
        service.requestOrder(id).enqueue(object : Callback<GetOrderResponse> {
            override fun onResponse(call: Call<GetOrderResponse>, response: Response<GetOrderResponse>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    onSuccess(responseBody.toDomain())
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_ORDER_FAILED })
                }
            }

            override fun onFailure(call: Call<GetOrderResponse>, t: Throwable) {
                onFailure(MESSAGE_GET_ORDER_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_GET_POINTS_FAILED = "포인트를 불러오는데 실패했습니다."
        private const val MESSAGE_GET_ORDER_HISTORIES_FAILED = "주문 목록을 불러오는데 실패했습니다."
        private const val MESSAGE_GET_ORDER_FAILED = "주문 정보를 불러오는데 실패했습니다."
    }
}