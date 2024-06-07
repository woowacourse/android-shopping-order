package woowacourse.shopping.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemRequest
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse

interface CartApiService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int,
    ): Response<CartItemResponse>

    @POST("/cart-items")
    suspend fun insertCartItem(
        @Header("accept") accept: String = "*/*",
        @Body cartItemRequest: CartItemRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
        @Body quantity: CartItemQuantityDto,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun requestCartItemCount(
        @Header("accept") accept: String = "*/*",
    ): Response<CartItemQuantityDto>
}
