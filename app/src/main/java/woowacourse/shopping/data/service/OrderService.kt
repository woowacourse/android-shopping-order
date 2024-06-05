package woowacourse.shopping.data.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.RequestOrdersPostDto

interface OrderService {
    @POST("/orders")
    suspend fun postOrders(
        @Body request: RequestOrdersPostDto,
    )
}
