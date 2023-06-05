package woowacourse.shopping.data.service.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.dto.OrderPostRequest
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.data.dto.OrdersResponse

interface OrderService {
    @POST("/orders")
    fun saveOrder(@Body orderPostRequest: OrderPostRequest): Call<Unit>

    @GET("/orders")
    fun getOrders(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<OrdersResponse> // 서버 이슈로 임시 반환 타입, 이후에 OrdersResponse로 변경 예정
}
