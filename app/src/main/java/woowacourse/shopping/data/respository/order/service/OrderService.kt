package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity

interface OrderService {
    @Headers(HEADER_JSON)
    @POST(PATH_ORDER_POST)
    fun requestPostOrder(
        @Header(AUTHORIZATION) token: String,
        @Body orderPost: OrderPostEntity,
    ): Call<Unit>

    @GET("$PATH_ORDER/{$PATH_ORDER_ID}")
    fun requestOrderById(
        @Header(AUTHORIZATION) token: String,
        @Path(PATH_ORDER_ID) orderId: Long,
    ): Call<OrderDetailEntity>

    @GET(PATH_ORDER)
    fun requestOrders(
        @Header(AUTHORIZATION) token: String,
    ): Call<List<OrderDetailEntity>>

    companion object {
        private const val HEADER_JSON = "Content-Type: application/json"

        private const val PATH_ORDER_ID = "id"
        private const val PATH_ORDER = "/orders"
        private const val PATH_ORDER_POST = "/orders"

        private const val AUTHORIZATION = "Authorization"
    }
}
