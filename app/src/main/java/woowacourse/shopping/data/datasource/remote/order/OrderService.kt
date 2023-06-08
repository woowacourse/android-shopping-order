package woowacourse.shopping.data.datasource.remote.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.order.OrderDetailDto
import woowacourse.shopping.data.model.order.OrderDetailProductDto
import woowacourse.shopping.data.model.order.OrderHistoryInfoDto

interface OrderService {

    @POST("/orders")
    fun addOrder(
        @Body orderInfo: OrderDetailProductDto
    ): Call<Unit>

    @DELETE("/orders/{orderId}")
    fun cancelOrder(
        @Path("orderId") orderId: Int,
    ): Call<Unit>

    @GET("/orders")
    fun requestOrderHistory(
        @Query("page") pageNum: Int
    ): Call<OrderHistoryInfoDto>

    @GET("/orders/{orderId}")
    fun requestOrderDetail(
        @Path("orderId") orderId: Int
    ): Call<OrderDetailDto>
}
