package woowacourse.shopping.data.source.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.source.remote.dto.cart.CartResponse

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

    @GET("/cart-items")
    suspend fun requestItems(
        @Header("Authorization") basicToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartResponse
}
