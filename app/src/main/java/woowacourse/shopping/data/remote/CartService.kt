package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse

interface CartService {
    @GET("/cart-items")
    fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Call<CartResponse>

    @POST("/cart-items")
    fun addCartItem(
        @Body cartItemRequestBody: CartItemRequestBody,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Int,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItem(
        @Path("id") id: Int,
        @Body cartQuantity: CartQuantity,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartTotalQuantity(): Call<CartQuantity>
}
