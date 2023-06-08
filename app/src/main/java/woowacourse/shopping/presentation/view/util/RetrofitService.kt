package woowacourse.shopping.presentation.view.util

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.model.PointEntity
import woowacourse.shopping.data.model.SavingPointEntity

interface RetrofitService {

    @Headers(HEADER_JSON)
    @GET(PATH_POINT)
    fun requestReservedPoint(
        @Header(AUTHORIZATION) token: String,
    ): Call<PointEntity>

    @Headers(HEADER_JSON)
    @GET(PATH_SAVING_POINT)
    fun requestSavingPoint(
        @Query(PATH_TOTAL_PRICE) totalPrice: String,
    ): Call<SavingPointEntity>

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
        private const val PATH_TOTAL_PRICE = "totalPrice"
        private const val PATH_POINT = "/points"
        private const val PATH_SAVING_POINT = "/saving-point"
        private const val PATH_ORDER = "/orders"
        private const val PATH_ORDER_POST = "/orders"

        private const val AUTHORIZATION = "Authorization"
    }
}
