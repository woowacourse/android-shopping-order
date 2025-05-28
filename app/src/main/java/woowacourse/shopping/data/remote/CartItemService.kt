package woowacourse.shopping.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.cart.CartItemCountResponse
import woowacourse.shopping.data.dto.cart.CartItemRequest
import woowacourse.shopping.data.dto.cart.CartsResponse
import woowacourse.shopping.data.dto.cart.UpdateCartRequest

interface CartItemService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartsResponse>

    @POST("/cart-items")
    fun addCartItem(
        @Body request: CartItemRequest,
    ): Call<ResponseBody>

    @PATCH("/cart-items/{id}")
    fun updateCartItem(
        @Path("id") cartId: Long,
        @Body updateCartRequest: UpdateCartRequest,
    ): Call<ResponseBody>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") cartId: Long,
    ): Call<ResponseBody>

    @GET("/cart-items/counts")
    fun requestCartItemCount(): Call<CartItemCountResponse>
}
