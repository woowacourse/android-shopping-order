package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.CartItemRequest
import woowacourse.shopping.data.remote.dto.CartResponse
import woowacourse.shopping.data.remote.dto.Quantity

interface CartService {
    @GET("cart-items")
    suspend fun getCartItems(
        @Header("Authorization") auth: String,
        @Query("page") pageIndex: Int = 0,
        @Query("size") size: Int = Int.MAX_VALUE,
    ): CartResponse

    @POST("cart-items")
    suspend fun addCartItem(
        @Header("Authorization") auth: String,
        @Body request: CartItemRequest,
    ): Response<Unit>

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("Authorization") auth: String,
        @Path("id") cartItemId: Long,
    )

    @PATCH("cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Header("Authorization") auth: String,
        @Path("id") cartItemId: Long,
        @Body quantity: Quantity,
    )

    @GET("cart-items/counts")
    suspend fun getTotalCount(
        @Header("Authorization") auth: String,
    ): Quantity
}
