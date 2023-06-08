package woowacourse.shopping.data.httpclient.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.httpclient.request.AddOrderRequest
import woowacourse.shopping.data.httpclient.response.order.Individualorder.IndividualOrderResponse

interface OrderService {
    @POST("/orders")
    fun addOrder(
        @Body body: AddOrderRequest
    ): Call<Unit>

    @GET("/orders/{id}")
    fun getIndividualOrderInfo(
        @Path("id") orderId: Int
    ): Call<IndividualOrderResponse>

    @GET("/orders")
    fun getOrdersInfo(): Call<List<IndividualOrderResponse>>
}
