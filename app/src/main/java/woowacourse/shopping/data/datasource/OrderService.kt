package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.DataOrderRecord
import woowacourse.shopping.data.model.OrderRequest

interface OrderService {

    @GET("orders/{id}")
    fun getOrderRecord(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        orderId: Int,
    ): Call<DataOrderRecord>

    @POST("orders")
    fun addOrder(
        @Header("Authorization")
        authorization: String,
        @Body
        orderRequest: OrderRequest,
    ): Call<DataOrderRecord>
}
