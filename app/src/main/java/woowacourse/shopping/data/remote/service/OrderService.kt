package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto

interface OrderService {
    @POST("/orders")
    suspend fun postOrders(
        @Body request: RequestOrderPostDto,
    ): Response<Unit>
}
