package woowacourse.shopping.data.source.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.source.remote.dto.cart.CartResponse

@Serializable
data class AddItemRequestBody(
    val productId: Long,
    val quantity: Int,
)

@Serializable
data class QuantityRequestBody(
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

    @DELETE("/cart-items/{id}")
    suspend fun requestDeleteItem(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long,
    )

    @PATCH("/cart-items/{id}")
    suspend fun requestChangeQuantity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long,
        @Body quantity: QuantityRequestBody,
    )
}
