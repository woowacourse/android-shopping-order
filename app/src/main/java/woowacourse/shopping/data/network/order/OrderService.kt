package woowacourse.shopping.data.network.order

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    suspend fun orders(
        @Header("accept")
        accept: String = "*/*",
        @Body
        cartItemIds: List<Long>,
    ): Response<Unit>
}
