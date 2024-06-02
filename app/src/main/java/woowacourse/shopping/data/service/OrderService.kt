package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.RequestOrdersPostDto

interface OrderService {
    @POST("/orders")
    fun postOrders(
        @Body request: RequestOrdersPostDto,
    ): Call<Unit>
}
