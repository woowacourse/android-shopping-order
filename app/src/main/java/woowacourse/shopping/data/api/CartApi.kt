package woowacourse.shopping.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsQuantityResponse
import woowacourse.shopping.data.model.response.CartItemsResponse

interface CartApi {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartItemsResponse

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body cartItemRequest: CartItemRequest,
    ): Unit

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    ): Unit

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItem(
        @Path("id") id: Long,
        @Body cartItemQuantityRequest: CartItemQuantityRequest,
    ): Unit

    @GET("/cart-items/counts")
    suspend fun getCartItemsCount(): CartItemsQuantityResponse
}
