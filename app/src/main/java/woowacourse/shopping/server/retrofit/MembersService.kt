package woowacourse.shopping.server.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import woowacourse.shopping.model.data.dto.OrderDTO
import woowacourse.shopping.model.data.dto.OrderDetailDTO
import woowacourse.shopping.model.data.dto.PointDTO

interface MembersService {

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @GET("/members/points")
    fun getPoint(): Call<PointDTO>

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @GET("/members/orders")
    fun getOrders(): Call<OrderDTO>

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @GET("/members/orders/{orderId}")
    fun getOrder(
        @Path("orderId") orderId: Long
    ): Call<OrderDetailDTO>
}
