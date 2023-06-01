package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderRemoteService {

    @Headers("Content-Type: application/json")
    @POST("orders")
    fun requestToPostOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequestBody: OrderRequestBody
    ): Call<Unit>

    @GET("orders/{orderId}")
    fun requestOrder(
        @Header("Authorization") authorization: String,
        @Path("orderId") orderId: Long
    ): Call<OrderDto>

    @GET("orders")
    fun requestOrders(@Header("Authorization") authorization: String): Call<List<OrderDto>>
}

data class OrderRequestBody(val cartItemIds: List<Long>)
