package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderDetailService {
    @GET("orders/{orderId}")
    fun getById(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long,
    ): Call<OrderDTO>
}
