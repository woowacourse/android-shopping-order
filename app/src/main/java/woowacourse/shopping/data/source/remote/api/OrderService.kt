package woowacourse.shopping.data.source.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class OrderRequestBody(
    val cartItemIds: List<Long>,
)

interface OrderService {
    @POST("/orders")
    suspend fun requestOrder(
        @Body orderRequestBody: OrderRequestBody,
    )
}
