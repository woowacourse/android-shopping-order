package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemService {
    @GET("/cart-items")
    fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartItemResponse>

    @POST("/cart-items")
    fun postCartItem(
        @Body body: CartItemRequest,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Path("id") id: Long,
        @Body body: Quantity,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemsCount(): Call<Quantity>
}
