package woowacourse.shopping.data.datasource.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.data.model.OrderRequest

interface OrderService {

    @GET("orders/{id}")
    fun getOrder(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        orderId: Int,
    ): Call<DataOrder>

    @POST("orders")
    fun addOrder(
        @Header("Authorization")
        authorization: String,
        @Body
        orderRequest: OrderRequest,
    ): Call<DataOrder>

    @GET("orders")
    fun getOrders(
        @Header("Authorization")
        authorization: String,
    ): Call<List<DataOrder>>
}
