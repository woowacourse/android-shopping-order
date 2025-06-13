package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.OrderRequestDto

interface OrderApiService {
    @POST("/orders")
    suspend fun insert(
        @Body body: OrderRequestDto,
    ): Response<Unit>
}
