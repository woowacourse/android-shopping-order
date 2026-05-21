package woowacourse.shopping.data.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApi {
    @POST("orders")
    suspend fun order(
        @Body
        requestBody: OrderRequest,
    )
}

@Serializable
data class OrderRequest(
    val cartItemIds: List<Int>,
)
