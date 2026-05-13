package woowacourse.shopping.data.source.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@Serializable
data class AddItemRequestBody(
    val productId: Long,
    val quantity: Int,
)

interface CartService {
    @POST("/cart-items")
    suspend fun requestAddItem(
        @Header("Authorization") basicToken: String,
        @Body addItemRequestBody: AddItemRequestBody,
    )
}
