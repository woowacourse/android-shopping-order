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
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface CartItemApi {
    @GET("cart-items")
    suspend fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): CartResponse

    @POST("cart-items")
    suspend fun addCartItem(
        @Header("accept") accept: String = "*/*",
        @Body cartItemRequest: CartItemRequest,
    ): Response<Unit>

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    )

    @PATCH("cart-items/{id}")
    suspend fun updateCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body quantityRequest: QuantityRequest,
    )

    @GET("cart-items/counts")
    suspend fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
    ): QuantityResponse
}
