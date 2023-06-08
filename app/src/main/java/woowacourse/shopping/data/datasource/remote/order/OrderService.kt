package woowacourse.shopping.data.datasource.remote.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.order.OrderDetailDto
import woowacourse.shopping.data.model.order.OrderDetailProductDto
import woowacourse.shopping.data.model.order.OrderHistoryInfoDto

interface OrderService {

    @Headers("Content-Type: application/json")
    @POST("/orders")
    fun addOrder(
        @Header("Authorization") credential: String,
        @Body orderInfo: OrderDetailProductDto
    ): Call<Unit>

    @DELETE("/orders/{orderId}")
    fun cancelOrder(
        @Header("Authorization") credential: String,
        @Path("orderId") orderId: Int,
    ): Call<Unit>

    @Headers("Content-Type: application/json")
    @GET("/orders")
    fun requestOrderHistory(
        @Header("Authorization") credential: String,
        @Query("page") pageNum: Int
    ): Call<OrderHistoryInfoDto>

    @GET("/orders/{orderId}")
    fun requestOrderDetail(
        @Header("Authorization") credential: String,
        @Path("orderId") orderId: Int
    ): Call<OrderDetailDto>
}
