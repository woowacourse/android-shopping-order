package woowacourse.shopping.data.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.CartResponseDto

interface CartApi {
    @GET("cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartResponseDto

    @GET("cart-items/counts")
    suspend fun getCartItemsCount(): Int

    @POST("cart-items")
    suspend fun addCartItem(
        @Body requestBody: AddCartItemRequest,
    )

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int,
    )

    @PATCH("cart-items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body requestBody: UpdateCartItemRequest,
    )
}

@Serializable
data class UpdateCartItemRequest(
    val quantity: Int,
)

@Serializable
data class AddCartItemRequest(
    val productId: Int,
    val quantity: Int,
)
