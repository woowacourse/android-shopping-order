package woowacourse.shopping.data.member

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.data.entity.OrderHistoryEntity
import woowacourse.shopping.data.entity.PointEntity

interface MemberService {
    @GET("members/points")
    fun requestPoints() : Call<PointEntity>

    @GET("members/orders")
    fun requestOrderHistories() : Call<List<OrderHistoryEntity>>

    @GET("members/orders/{orderId}")
    fun requestOrder(
        @Path("orderId") orderId: Int
    ) : Call<OrderEntity>
}