package woowacourse.shopping.data.member

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.member.response.GetOrderResponse
import woowacourse.shopping.data.member.response.GetOrderHistoryResponse
import woowacourse.shopping.data.member.response.GetPointsResponse

interface MemberService {
    @GET("members/points")
    fun requestPoints() : Call<GetPointsResponse>

    @GET("members/orders")
    fun requestOrderHistories() : Call<List<GetOrderHistoryResponse>>

    @GET("members/orders/{orderId}")
    fun requestOrder(
        @Path("orderId") orderId: Int
    ) : Call<GetOrderResponse>
}