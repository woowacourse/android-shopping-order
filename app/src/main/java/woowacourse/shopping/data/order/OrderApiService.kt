package woowacourse.shopping.data.order

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.OrderRequestDto

interface OrderApiService {
    @POST("/orders")
    suspend fun requestOrder(
        @Body orderRequest: OrderRequestDto,
    )
}
