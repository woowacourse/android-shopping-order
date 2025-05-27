package woowacourse.shopping.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.model.request.CartItemRequest
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
    ): Call<String?>
}
