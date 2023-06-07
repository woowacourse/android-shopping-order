package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.dto.request.OrderRequest
import woowacourse.shopping.data.model.dto.response.OrderDetailResponse
import woowacourse.shopping.data.model.dto.response.PointResponse
import woowacourse.shopping.data.model.dto.response.SavingPointResponse

interface OrderService {
    @POST("/orders")
    fun requestPostData(
        @Body
        order: OrderRequest,
    ): Call<Unit>

    @GET("/orders/{orderId}")
    fun requestOrderItem(
        @Path("orderId")
        orderId: Long,
    ): Call<OrderDetailResponse>

    @GET("/orders")
    fun requestOrderList(): Call<List<OrderDetailResponse>>

    @GET("/points")
    fun requestPoint(): Call<PointResponse>

    @GET("/saving-point?")
    fun requestPredictionSavePoint(
        @Query("totalPrice")
        totalPrice: Int,
    ): Call<SavingPointResponse>
}
