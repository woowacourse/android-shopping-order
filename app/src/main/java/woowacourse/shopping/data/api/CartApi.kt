package woowacourse.shopping.data.api

import retrofit2.Call
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
    fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
    ): Call<CartItemsResponse>

    @POST("/cart-items")
    fun postCartItem(
        @Body cartItemRequest: CartItemRequest,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Path("id") id: Long,
        @Body cartItemQuantityRequest: CartItemQuantityRequest,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemsCount(): Call<CartItemsQuantityResponse>
}
