package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity

interface OrderService {
    @POST(PATH_ORDER_POST)
    fun requestPostOrder(
        @Body orderPost: OrderPostEntity,
    ): Call<Unit>

    @GET("$PATH_ORDER/{$PATH_ORDER_ID}")
    fun requestOrderById(
        @Path(PATH_ORDER_ID) orderId: Long,
    ): Call<OrderDetailEntity>

    @GET(PATH_ORDER)
    fun requestOrders(): Call<List<OrderDetailEntity>>

    companion object {
        private const val PATH_ORDER_ID = "id"
        private const val PATH_ORDER = "/orders"
        private const val PATH_ORDER_POST = "/orders"
    }
}
